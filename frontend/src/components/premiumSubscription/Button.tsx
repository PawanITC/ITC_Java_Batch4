import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary';
}

export const Button: React.FC<ButtonProps> = ({ children, variant = 'primary', className = '', ...props }) => {
  const baseStyle = "px-6 py-2.5 font-semibold rounded-full transition-all duration-200 text-sm tracking-wide";
  const variants = {
    primary: "bg-[#0a66c2] text-white hover:bg-[#004182] active:bg-[#002244]", // LinkedIn Blue colors
    secondary: "border border-[#0a66c2] text-[#0a66c2] hover:bg-[#f3f6f8]"
  };

  return (
    <button className={`${baseStyle} ${variants[variant]} ${className}`} {...props}>
      {children}
    </button>
  );
};