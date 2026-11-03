import { useEffect, useState } from 'react'

function Tarifas() {
  const [tarifas, setTarifas] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    async function cargar() {
      try {
        const respuesta = await fetch('/api/tarifas')
        if (!respuesta.ok) throw new Error(`HTTP ${respuesta.status}`)
        const lista = await respuesta.json()

        // Pedimos a la vez los 24 precios de hoy de cada tarifa
        const conPrecios = await Promise.all(
          lista.map(async (tarifa) => {
            const r = await fetch(`/api/tarifas/${tarifa.id}/precios`)
            if (!r.ok) throw new Error(`HTTP ${r.status}`)
            return { ...tarifa, precios: await r.json() }
          }),
        )
        setTarifas(conPrecios)
        setError(null)
      } catch (e) {
        setError('No se puede contactar con tariff-service: ' + e.message)
      }
    }

    cargar()
  }, [])

  // Hora del navegador. Madrid y Zúrich comparten huso horario.
  const horaActual = new Date().getHours()

  if (error) return <p className="error">{error}</p>
  if (tarifas.length === 0) return <p>Cargando tarifas…</p>

  return (
    <section>
      <h2>Precio del kWh hoy</h2>

      <div className="tarjetas">
        {tarifas.map((tarifa) => (
          <article key={tarifa.id} className="tarjeta">
            <h3>{tarifa.nombre}</h3>
            <p className="valor">{tarifa.precios[horaActual].precioKwh.toFixed(2)} CHF</p>
            <p className="descripcion">{tarifa.descripcion}</p>
          </article>
        ))}
      </div>

      <table>
        <thead>
          <tr>
            <th>Hora</th>
            {tarifas.map((tarifa) => (
              <th key={tarifa.id}>{tarifa.nombre} (CHF/kWh)</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {Array.from({ length: 24 }, (_, hora) => {
            // La tarifa más barata de cada hora se marca en verde
            const minimo = Math.min(...tarifas.map((t) => t.precios[hora].precioKwh))
            return (
              <tr key={hora} className={hora === horaActual ? 'ahora' : undefined}>
                <td>{String(hora).padStart(2, '0')}:00</td>
                {tarifas.map((tarifa) => {
                  const precio = tarifa.precios[hora].precioKwh
                  return (
                    <td key={tarifa.id} className={precio === minimo ? 'mas-barata' : undefined}>
                      {precio.toFixed(2)}
                    </td>
                  )
                })}
              </tr>
            )
          })}
        </tbody>
      </table>
    </section>
  )
}

export default Tarifas