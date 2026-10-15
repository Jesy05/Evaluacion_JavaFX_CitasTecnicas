# Cronograma real del equipo

Registro de cuándo se trabajó cada parte, tomado del historial de Git (`git log`). Los renglones
marcados como _completar_ los debe escribir el equipo con lo que realmente ocurrió.

## Contexto

- El repositorio se creó el 14/10/2026 con la estructura inicial del proyecto.
- _Completar: explicar en una o dos frases por qué el trabajo en el repositorio comenzó ese día._

## Línea de tiempo

| Fecha | Qué se hizo | Quién | Evidencia |
|-------|-------------|-------|-----------|
| 12/10/2026 | _completar con lo que realmente se hizo; si no hubo trabajo en el repositorio, dejarlo escrito así_ | _..._ | _..._ |
| 13/10/2026 | _completar_ | _..._ | _..._ |
| 14/10/2026 | Estructura inicial, script SQL, conexión JDBC, modelos y ventana principal | Jesy | PR #1 |
| 14/10/2026 | Validaciones, alertas y pruebas unitarias del `Validador` | Gabriela | PR #2 y #3 |
| 14/10/2026 | CRUD de clientes: `ClienteDAO`, formulario FXML y búsqueda | José | PR #4 y #5 |
| 14/10/2026 | CRUD de citas: `CitaTecnicaDao`, formulario FXML y filtros | Fabiola | PR #6 y #7 |
| 15/10/2026 | Datos de prueba y pruebas de conexión y modelos | José | PR #8 |
| 15/10/2026 | Pruebas en `src/test`, corrección de horas inválidas y documentación | Jesy | Rama `feat/pruebas-borde-y-docs-defensa` |

## Distribución de responsabilidades

| Integrante | Usuario GitHub | Parte entregada |
|------------|----------------|-----------------|
| Jesy González | @Jesy05 | Base del proyecto, script SQL, conexión JDBC, modelos, ventana principal |
| Gabriela Espinoza | @gabi122007 | Validaciones, alertas y pruebas unitarias |
| Fabiola Saraí Lanuza Paz | @FB1820 | CRUD de citas, ComboBox y filtros |
| José Cristo Carvallo Herrera | @JoseCristoCarvalloHerrera | CRUD de clientes, búsqueda y datos de prueba |

## Cómo verificar la autoría

```bash
git log --format="%h | %an | %ad | %s" --date=short
git shortlog -sn
```
