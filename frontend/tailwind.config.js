/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        poppins: ["Poppins", "sans-serif"],
      },
      colors: {
        "primary-pink": "#FF69B4", // A fun, vibrant pink
        "secondary-teal": "#20C997", // A fresh teal accent
        "dark-gray": "#343a40", // For text and dark elements
        "light-gray": "#f8f9fa", // For backgrounds
      },
    },
  },
  plugins: [],
};
