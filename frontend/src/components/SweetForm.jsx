import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";

const SweetForm = ({ currentSweet, onSuccess, onCancel }) => {
  const { token, API_BASE_URL } = useAuth();
  const isEditMode = !!currentSweet;
  const [formData, setFormData] = useState({
    name: "",
    category: "",
    price: "",
    quantity: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEditMode) {
      setFormData({
        name: currentSweet.name,
        category: currentSweet.category,
        price: currentSweet.price.toString(),
        quantity: currentSweet.quantity.toString(),
      });
    } else {
      setFormData({ name: "", category: "", price: "", quantity: "" });
    }
  }, [currentSweet, isEditMode]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    const url = isEditMode
      ? `${API_BASE_URL}/sweets/${currentSweet.id}`
      : `${API_BASE_URL}/sweets`;
    const method = isEditMode ? "PUT" : "POST";

    // Convert price and quantity to correct types
    const payload = {
      ...formData,
      price: parseFloat(formData.price),
      quantity: parseInt(formData.quantity),
    };

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorBody = await response.text();
        throw new Error(
          errorBody || `${isEditMode ? "Update" : "Creation"} failed`
        );
      }

      alert(`Sweet ${isEditMode ? "updated" : "created"} successfully!`);
      onSuccess();
    } catch (err) {
      setError(err.message.split(" ").slice(0, 15).join(" ") + "..."); // Truncate long Spring validation errors
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-xl mb-8 border-t-4 border-teal-500">
      <h2 className="text-2xl font-bold mb-4 text-teal-700">
        {isEditMode ? "Edit Sweet" : "Add New Sweet"}
      </h2>
      {error && (
        <p className="text-red-500 mb-4 text-sm font-medium bg-red-100 p-2 rounded">
          {error}
        </p>
      )}

      <form
        onSubmit={handleSubmit}
        className="grid grid-cols-1 md:grid-cols-2 gap-4"
      >
        {/* Name */}
        <div>
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Name
          </label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:ring-teal-500"
          />
        </div>
        {/* Category */}
        <div>
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Category
          </label>
          <input
            type="text"
            name="category"
            value={formData.category}
            onChange={handleChange}
            required
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:ring-teal-500"
          />
        </div>
        {/* Price */}
        <div>
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Price ($)
          </label>
          <input
            type="number"
            step="0.01"
            name="price"
            value={formData.price}
            onChange={handleChange}
            required
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:ring-teal-500"
          />
        </div>
        {/* Quantity */}
        <div>
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Quantity
          </label>
          <input
            type="number"
            name="quantity"
            value={formData.quantity}
            onChange={handleChange}
            required
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:ring-teal-500"
          />
        </div>

        {/* Buttons */}
        <div className="md:col-span-2 flex justify-end space-x-4 mt-4">
          {isEditMode && (
            <button
              type="button"
              onClick={onCancel}
              className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded transition duration-150"
              disabled={loading}
            >
              Cancel Edit
            </button>
          )}
          <button
            type="submit"
            className="bg-teal-500 hover:bg-teal-700 text-white font-bold py-2 px-4 rounded transition duration-150"
            disabled={loading}
          >
            {loading
              ? "Processing..."
              : isEditMode
              ? "Update Sweet"
              : "Create Sweet"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default SweetForm;
