import { useEffect, useState } from 'react'
import './App.css'
import Tarifas from './Tarifas.jsx'

const CADA_CUANTO_MS = 5000

function App() {
  const [lecturas, setLecturas] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    async function cargar() {
      try {
        const respuesta = await fetch('/api/lecturas')
        if (!respuesta.ok) throw new Error(`HTTP ${respuesta.status}`)
        setLecturas(await respuesta.json())
        setError(null)
      } catch (e) {
        setError('No se puede contactar con consumption-service: ' + e.message)
      }
    }

    cargar()
    const temporizador = setInterval(cargar, CADA_CUANTO_MS)
    return () => clearInterval(temporizador) // limpieza al salir de la página
  }, [])

  // Las lecturas llegan de la más nueva a la más vieja:
  // la primera que vemos de cada medidor es la última que envió.
  const ultimaPorMedidor = {}
  for (const lectura of lecturas) {
    if (!ultimaPorMedidor[lectura.medidorId]) {
      ultimaPorMedidor[lectura.medidorId] = lectura
    }
  }

  return (
    <main>
      <h1>Panel de consumo</h1>
      <p className="subtitulo">Se actualiza cada {CADA_CUANTO_MS / 1000} segundos</p>

      {error && <p className="error">{error}</p>}

      <section className="tarjetas">
        {Object.values(ultimaPorMedidor).map((lectura) => (
          <article key={lectura.medidorId} className="tarjeta">
            <h2>{lectura.medidorId}</h2>
            <p className="valor">{lectura.kwh.toFixed(3)} kWh</p>
            <p className="hora">{new Date(lectura.momento).toLocaleTimeString()}</p>
          </article>
        ))}
      </section>

      <h2>Últimas lecturas</h2>
      <table>
        <thead>
          <tr>
            <th>Medidor</th>
            <th>Hora</th>
            <th>Consumo (kWh)</th>
          </tr>
        </thead>
        <tbody>
          {lecturas.map((lectura) => (
            <tr key={lectura.id}>
              <td>{lectura.medidorId}</td>
              <td>{new Date(lectura.momento).toLocaleTimeString()}</td>
              <td>{lectura.kwh.toFixed(3)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <Tarifas />
    </main>
  )
}

export default App