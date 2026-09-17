interface FormAlertProps {
  mensaje: string;
  detalles?: string[];
}

export function FormAlert({ mensaje, detalles }: FormAlertProps) {
  return (
    <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-700" role="alert">
      <p>{mensaje}</p>
      {detalles && detalles.length > 0 && (
        <ul className="mt-1 list-disc pl-5">
          {detalles.map((detalle) => (
            <li key={detalle}>{detalle}</li>
          ))}
        </ul>
      )}
    </div>
  );
}
