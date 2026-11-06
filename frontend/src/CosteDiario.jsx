import { useEffect, useState } from 'react'

const CADA_CUANTO_MS = 30000

function CosteDiario() {
  const [medidores, setMedidores] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    async function cargar() {
      try {
        const respuesta = await fetch('/api/consumo/diario')
        if (!respuesta.ok) {
          // Si el backend devuelve un ProblemDetail, mostramos su explicación
          const problema = await respuesta.json().catch(() => null)
          throw new Error(problema?.detail ?? `HTTP ${respuesta.status}`)
        }
        setMedidores(await respuesta.json())
        setError(null)
      } catch (e) {
        setError('No se puede calcular el coste: ' + e.message)
      }
    }

    cargar()
    const temporizador = setInterval(cargar, CADA_CUANTO_MS)
    return () => clearInterval(temporizador)
  }, [])

  if (error) return <p className="error">{error}</p>
  if (medidores.length === 0) return <p>Todavía no hay consumo hoy.</p>

  // Todos los contadores traen las mismas tarifas y en el mismo orden
  const tarifas = medidores[0].costes

  return (
    <section>
      <h2>Coste de hoy por contador</h2>
      <p className="subtitulo">
        Lo que pagaría cada contador por lo consumido hoy con cada tarifa. En verde, la más barata.
      </p>
      <table>
        <thead>
          <tr>
            <th>Medidor</th>
            <th>Consumo (kWh)</th>
            {tarifas.map((tarifa) => (
              <th key={tarifa.tarifaId}>{tarifa.tarifa} (CHF)</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {medidores.map((medidor) => {
            const minimo = Math.min(...medidor.costes.map((c) => c.chf))
            return (
              <tr key={medidor.medidorId}>
                <td>{medidor.medidorId}</td>
                <td>{medidor.kwh.toFixed(3)}</td>
                {medidor.costes.map((coste) => (
                  <td key={coste.tarifaId} className={coste.chf === minimo ? 'mas-barata' : undefined}>
                    {coste.chf.toFixed(3)}
                  </td>
                ))}
              </tr>
            )
          })}
        </tbody>
      </table>
    </section>
  )
}

export default CosteDiario