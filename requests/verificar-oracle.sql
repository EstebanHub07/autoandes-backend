-- Ejecutar ANTES y DESPUÉS de la solicitud con tercer detalle inactivo.
SELECT COUNT(*) AS cabeceras FROM orden_servicio;
SELECT COUNT(*) AS detalles FROM detalle_orden;
SELECT o.numero, o.total, SUM(d.subtotal) AS suma_detalles
FROM orden_servicio o JOIN detalle_orden d ON d.orden_id = o.id
GROUP BY o.numero, o.total ORDER BY o.numero;
-- No debe devolver filas.
SELECT d.id FROM detalle_orden d LEFT JOIN orden_servicio o ON o.id=d.orden_id WHERE o.id IS NULL;
-- Permite comprobar que el reporte excluye las anuladas.
SELECT s.codigo, s.nombre, SUM(d.cantidad) cantidad_total, SUM(d.subtotal) importe_total
FROM detalle_orden d JOIN servicio s ON s.id=d.servicio_id
JOIN orden_servicio o ON o.id=d.orden_id WHERE o.estado='REGISTRADA'
GROUP BY s.codigo,s.nombre ORDER BY importe_total DESC;

