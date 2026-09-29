import type { ReactNode } from "react";

export type BadgeVariante = "neutral" | "success" | "warning" | "danger" | "brand";

const ESTILOS: Record<BadgeVariante, string> = {
  neutral: "bg-neutral-100 text-neutral-700",
  success: "bg-green-50 text-green-700",
  warning: "bg-amber-50 text-amber-700",
  danger: "bg-red-50 text-red-700",
  brand: "bg-brand-50 text-brand-700",
};

interface BadgeProps {
  children: ReactNode;
  variante?: BadgeVariante;
}

export function Badge({ children, variante = "neutral" }: BadgeProps) {
  return (
    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${ESTILOS[variante]}`}>
      {children}
    </span>
  );
}
