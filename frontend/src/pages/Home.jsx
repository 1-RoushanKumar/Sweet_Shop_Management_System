import React, { useState, useEffect, useCallback } from "react";
import SweetCard from "../components/SweetCard"; // To be created
import { useAuth } from "../context/AuthContext";

const Home = () => {
  const { token, API_BASE_URL } = useAuth();
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    name: "",
    category: "",
    minPrice: "",
    maxPrice: "",
  });

  const fetchSweets = useCallback(
    async (params) => {
      setLoading(true);
      const query = new URLSearchParams(params).toString();
      const url = `${API_BASE_URL}/sweets${query ? `/search?${query}` : ""}`;

      try {
        const response = await fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            ...(token && { Authorization: `Bearer ${token}` }),
          },
        });

        if (!response.ok) {
          // Handle 403 Forbidden if not logged in for /search
          if (response.status === 403) {
            console.error(
              "Access denied. Displaying general sweets endpoint result."
            );
            // Fallback to fetching all sweets if search is protected
            return fetchSweets({});
          }
          throw new Error("Failed to fetch sweets");
        }
        const data = await response.json();
        setSweets(data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    },
    [token, API_BASE_URL]
  );

  useEffect(() => {
    // Initial fetch: fetch all sweets
    fetchSweets({});
  }, [fetchSweets]);

  const handleSearchChange = (e) => {
    setSearchParams({ ...searchParams, [e.target.name]: e.target.value });
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    // Filter out empty params for clean query string
    const activeParams = Object.fromEntries(
      Object.entries(searchParams).filter(([, v]) => v !== "")
    );
    fetchSweets(activeParams);
  };

  const handlePurchase = async (id) => {
    if (!token) {
      alert("Please log in to make a purchase.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/sweets/${id}/purchase`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Purchase failed");
      }

      // Update the state to reflect the quantity change
      setSweets((prevSweets) =>
        prevSweets.map((sweet) =>
          sweet.id === id ? { ...sweet, quantity: sweet.quantity - 1 } : sweet
        )
      );
      alert("Sweet purchased successfully!");
    } catch (error) {
      alert(`Purchase error: ${error.message}`);
    }
  };

  if (loading)
    return <div className="text-center text-xl mt-10">Loading sweets...</div>;

  return (
    <div className="py-8">
      <h1 className="text-4xl font-extrabold text-gray-800 mb-6 border-b pb-2">
        Our Sweet Selection
      </h1>

      {/* Search/Filter Form */}
      <form
        onSubmit={handleSearchSubmit}
        className="grid grid-cols-1 md:grid-cols-5 gap-4 p-4 bg-gray-100 rounded-lg shadow-inner mb-8"
      >
        <input
          type="text"
          name="name"
          placeholder="Search by Name"
          onChange={handleSearchChange}
          value={searchParams.name}
          className="p-2 border rounded"
        />
        <input
          type="text"
          name="category"
          placeholder="Filter by Category"
          onChange={handleSearchChange}
          value={searchParams.category}
          className="p-2 border rounded"
        />
        <input
          type="number"
          name="minPrice"
          placeholder="Min Price"
          onChange={handleSearchChange}
          value={searchParams.minPrice}
          className="p-2 border rounded"
        />
        <input
          type="number"
          name="maxPrice"
          placeholder="Max Price"
          onChange={handleSearchChange}
          value={searchParams.maxPrice}
          className="p-2 border rounded"
        />
        <button
          type="submit"
          className="bg-pink-500 hover:bg-pink-600 text-white font-bold py-2 rounded transition duration-150"
        >
          Search
        </button>
      </form>

      {/* Sweet List */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {sweets.length > 0 ? (
          sweets.map((sweet) => (
            <SweetCard
              key={sweet.id}
              sweet={sweet}
              onPurchase={() => handlePurchase(sweet.id)}
            />
          ))
        ) : (
          <p className="text-gray-500 text-lg col-span-full text-center">
            No sweets found matching your criteria.
          </p>
        )}
      </div>
    </div>
  );
};

export default Home;
