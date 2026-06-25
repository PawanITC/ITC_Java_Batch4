import { useState } from "react";

type Props = {
  name: string;
  sizeClassName?: string;
  textClassName?: string;
  src?: string;
  alt?: string;
};

const PALETTES = [
  "bg-[#dbeafe] text-[#1d4ed8]",
  "bg-[#dcfce7] text-[#166534]",
  "bg-[#fce7f3] text-[#be185d]",
  "bg-[#ede9fe] text-[#6d28d9]",
  "bg-[#fee2e2] text-[#b91c1c]",
  "bg-[#fef3c7] text-[#b45309]",
];

function initials(name: string) {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();
}

function paletteFor(name: string) {
  const seed = [...name].reduce((sum, char) => sum + char.charCodeAt(0), 0);
  return PALETTES[seed % PALETTES.length];
}

export default function Avatar({
  name,
  sizeClassName = "h-12 w-12",
  textClassName = "text-sm",
  src,
  alt,
}: Props) {
  const [imageFailed, setImageFailed] = useState(false);

  if (src && !imageFailed) {
    return (
      <img
        src={src}
        alt={alt ?? name}
        onError={() => setImageFailed(true)}
        className={`${sizeClassName} rounded-full object-cover object-top`}
      />
    );
  }

  return (
    <div
      aria-label={alt ?? name}
      className={`flex shrink-0 items-center justify-center rounded-full font-semibold ${paletteFor(
        name
      )} ${sizeClassName} ${textClassName}`}
    >
      {initials(name) || "IN"}
    </div>
  );
}
