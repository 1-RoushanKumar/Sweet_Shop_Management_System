import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { user, logout, isAdmin } = useAuth();

  return (
    <nav className="bg-pink-600 p-4 shadow-lg">
      <div className="container mx-auto flex justify-between items-center">
        <Link
          to="/"
          className="text-white text-3xl font-extrabold tracking-wider"
        >
          Sweet Shop 🍬
        </Link>
        <div className="space-x-4 flex items-center">
          {user ? (
            <>
              <span className="text-white text-lg font-medium">
                Hello, {user} ({isAdmin ? "Admin" : "User"})
              </span>
              {isAdmin && (
                <Link
                  to="/admin"
                  className="text-yellow-200 hover:text-yellow-400 font-bold transition duration-150"
                >
                  Admin Panel
                </Link>
              )}
              <button
                onClick={logout}
                className="bg-white text-pink-600 hover:bg-pink-100 font-bold py-1 px-3 rounded transition duration-150"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="text-white hover:text-pink-200 font-medium"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="bg-white text-pink-600 hover:bg-pink-100 font-bold py-1 px-3 rounded transition duration-150"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
