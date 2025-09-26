import React, { useState, useEffect, useCallback } from "react";
import { useAuth } from "../context/AuthContext";
import SweetForm from "../components/SweetForm"; // To be created

const AdminPanel = () => {
  const { token, isAdmin, API_BASE_URL } = useAuth();
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingSweet, setEditingSweet] = useState(null); // Sweet object for update/null for create

  const fetchAllSweets = useCallback(async () => {
    if (!isAdmin || !token) {
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/sweets`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) throw new Error("Failed to fetch sweets");
      const data = await response.json();
      setSweets(data);
    } catch (error) {
      console.error("Error fetching sweets:", error);
    } finally {
      setLoading(false);
    }
  }, [token, isAdmin, API_BASE_URL]);

  useEffect(() => {
    fetchAllSweets();
  }, [fetchAllSweets]);

  const handleFormSuccess = () => {
    setEditingSweet(null); // Close form
    fetchAllSweets(); // Refresh list
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this sweet?")) return;

    try {
      const response = await fetch(`${API_BASE_URL}/sweets/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) throw new Error("Delete failed");

      alert("Sweet deleted successfully.");
      fetchAllSweets();
    } catch (error) {
      alert(`Delete error: ${error.message}`);
    }
  };

  const handleRestock = async (id) => {
    try {
      const response = await fetch(`${API_BASE_URL}/sweets/${id}/restock`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) throw new Error("Restock failed");

      alert("Sweet restocked successfully (+1).");
      // Optimistic update or refresh
      fetchAllSweets();
    } catch (error) {
      alert(`Restock error: ${error.message}`);
    }
  };

  if (!isAdmin) {
    return (
      <div className="mt-10 text-center text-red-500 text-xl">
        Access Denied. Admin privileges required.
      </div>
    );
  }

  if (loading)
    return (
      <div className="text-center text-xl mt-10">Loading Admin Panel...</div>
    );

  return (
    <div className="py-8">
      <h1 className="text-4xl font-extrabold text-gray-800 mb-6 border-b pb-2">
        Admin Panel
      </h1>

      {/* Sweet Creation/Update Form */}
      <SweetForm
        currentSweet={editingSweet}
        onSuccess={handleFormSuccess}
        onCancel={() => setEditingSweet(null)}
      />

      <h2 className="text-3xl font-bold text-gray-700 mt-10 mb-4">
        Current Sweets
      </h2>
      <div className="overflow-x-auto bg-white rounded-lg shadow-lg">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                ID
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Category
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Price
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Quantity
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {sweets.map((sweet) => (
              <tr key={sweet.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {sweet.id}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {sweet.name}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {sweet.category}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {sweet.price.toLocaleString("en-IN", {
                    style: "currency",
                    currency: "INR",
                  })}
                </td>

                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  <span
                    className={
                      sweet.quantity <= 5
                        ? "text-red-500 font-bold"
                        : "text-green-600"
                    }
                  >
                    {sweet.quantity}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                  <button
                    onClick={() => setEditingSweet(sweet)}
                    className="text-indigo-600 hover:text-indigo-900 transition duration-150"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(sweet.id)}
                    className="text-red-600 hover:text-red-900 transition duration-150"
                  >
                    Delete
                  </button>
                  <button
                    onClick={() => handleRestock(sweet.id)}
                    className="bg-green-100 text-green-700 hover:bg-green-200 py-1 px-2 rounded text-xs font-bold transition duration-150"
                  >
                    +1 Restock
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminPanel;
