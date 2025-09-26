import React from "react";
import { useAuth } from "../context/AuthContext";

const SweetCard = ({ sweet, onPurchase }) => {
  const { user } = useAuth();

  const isOutOfStock = sweet.quantity <= 0;
  const purchaseDisabled = isOutOfStock || !user;

  return (
    <div className="bg-white rounded-xl shadow-lg hover:shadow-xl transition duration-300 overflow-hidden border border-gray-200">
      <div className="p-6">
        <h3 className="text-2xl font-bold text-gray-800 mb-1">{sweet.name}</h3>
        <p className="text-pink-500 text-lg font-semibold mb-3">
          {sweet.price.toLocaleString("en-IN", {
            style: "currency",
            currency: "INR",
          })}
        </p>

        <div className="text-sm text-gray-600 space-y-1 mb-4">
          <p>
            Category:{" "}
            <span className="font-medium text-gray-700">{sweet.category}</span>
          </p>
          <p>
            Stock:{" "}
            <span
              className={`font-bold ${
                isOutOfStock ? "text-red-500" : "text-green-600"
              }`}
            >
              {isOutOfStock ? "Out of Stock" : `${sweet.quantity} remaining`}
            </span>
          </p>
        </div>

        <button
          onClick={onPurchase}
          disabled={purchaseDisabled}
          className={`w-full font-bold py-2 px-4 rounded transition duration-150 ${
            purchaseDisabled
              ? "bg-gray-400 text-gray-700 cursor-not-allowed"
              : "bg-teal-500 hover:bg-teal-600 text-white"
          }`}
        >
          {isOutOfStock ? "Sold Out" : user ? "Purchase" : "Login to Purchase"}
        </button>
      </div>
    </div>
  );
};

export default SweetCard;
