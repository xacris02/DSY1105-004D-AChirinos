# DSY1105-004D Alvaro Chirinos Matias Estobar

## Guía 14 – Consumo de API REST (Retrofit + MVVM)
Pantalla de posts (`https://jsonplaceholder.typicode.com/`), accesible desde Home con el botón **Ver posts (API REST)**.

- `data/model/Post.kt`, `data/remote/ApiService.kt` (GET/POST/PUT/DELETE), `data/remote/RetrofitInstance.kt`
- `repository/PostRepository.kt`, `viewmodel/PostViewModel.kt`, `ui/screens/PostScreen.kt`

## Guía 15 – Testing
- `src/test`: Kotest + MockK + coroutines-test (`PostViewModelTest`, `PostRepositoryTest`) → `./gradlew testDebugUnitTest`
- `src/androidTest`: Compose UI Test (`PostScreenTest`) → requiere emulador/dispositivo, `./gradlew connectedDebugAndroidTest`

Commit sugerido (Guía 15): `Creación de unit test`

## Registro + Firebase (Authentication)
- Pantalla de inicio (`BienvenidaScreen`): logo, **Iniciar sesión** y **Registrarse**.
- Registro (`RegistroScreen`): nombre, contraseña y confirmar contraseña, más **Registrarse con Google**.
- Firebase Auth: `repository/UsuarioRepository.kt` (correo/contraseña y Google con Credential Manager).
  Firebase identifica por correo, así que un nombre sin `@` se convierte internamente en `nombre@modoguardian.app`.
- Gradle 8.3 (AGP 8.2.2), `compileSdk`/`targetSdk` 35 (Android 15 "Vanilla Ice Cream").

### Configuración de Firebase (obligatoria para que funcione Google)
1. Firebase → Authentication → Sign-in method: habilita **Correo/contraseña** y **Google**.
2. Firebase → Configuración del proyecto → tu app Android (`com.example.insercodechirinosestobar`) → agrega la huella **SHA-1** (`./gradlew signingReport`, variante debug).
3. Descarga de nuevo `google-services.json` (ahora debe traer `oauth_client`) y ponlo en `app/google-services.json` (en `app/`, **no** en `app/src/`).
4. Copia el **ID de cliente web** (Google Cloud → Credenciales → "Web client (auto created by Google Service)", o en Firebase → Authentication → Google → Web SDK configuration) y pégalo en `app/src/main/res/values/strings.xml` → `google_web_client_id`.
5. Prueba en un emulador/celular **con Google Play** y una cuenta de Google ya agregada.

### Pantalla de inicio
Iniciar sesión, Registrarse (sin Google) y **Continuar con Google** (crea la cuenta si es nueva o inicia sesión si ya existe). El login también tiene "Iniciar sesión con Google".

## InsertCode · Ventas & Distribución Móvil
Paleta propia: verde bosque oscuro + teal + ámbar (`ui/theme/Theme.kt`). Pantallas: **Login, Registro, Dashboard, Pedidos, Catálogo, Clientes, Ruta GPS, Usuarios y Perfil**.
Arquitectura: MVVM, `NavigationViewModel` + `AppRoutes`, Firebase Auth, Room, Retrofit (Posts).

### Login / Google
- Solo hay botón de **Google** (no hay Apple). Login y Registro usan `UsuarioRepository.autenticarConGoogle`.
- Reintenta con el selector clásico de cuentas si el botón "Sign in with Google" no devuelve cuenta, y muestra el error real
  (antes un fallo de Firebase aparecía como "revisa los datos").
- Si falla: confirma Google habilitado en Firebase Authentication, la huella **SHA-1** de TU equipo registrada en Firebase,
  `google-services.json` actualizado en `app/` y un emulador/celular con Google Play y una cuenta agregada.

### Roles (cada uno ve un menú distinto)
| Rol | Menú | Puede |
|---|---|---|
| Administrador | Usuarios · Dashboard · Perfil | Crear usuarios de prueba, cambiar roles, activar/desactivar, ver roles y permisos |
| Supervisor | Dashboard · Pedidos · Ruta GPS · Perfil | Indicadores comerciales y logísticos; estados de pedidos y entregas (solo lectura) |
| Vendedor | Dashboard · Pedidos · Catálogo · Clientes · Perfil | Consultar clientes/productos/pedidos/ventas; cotizar, confirmar o cancelar sus pedidos |
| Distribuidor | Ruta GPS · Perfil | Ver sus entregas y rutas; actualizar estado (Pendiente → En ruta → Entregado) |


El rol se guarda en Room (tabla `usuarios`). Usuarios de prueba (crea la misma cuenta en Firebase con cualquier contraseña de 6+ caracteres):
`admin@`, `supervisor@`, `vendedor@`, `repartidor@` + `insertcode.cl`. Un correo nuevo parte como Vendedor (o por prefijo: admin…, supervisor…, repartidor…/distribuidor…).
Un usuario desactivado por el Administrador no puede entrar. 

Correos ya en la firebase:

admin@insertcode.cl

supervisor@insertcode.cl 

repartidor@insertcode.cl

contraseña: **123456**

### Flujo de datos
Vendedor cotiza (Catálogo) → pedido *Cotizado* → *Confirmar* crea una entrega pendiente → Distribuidor la toma/actualiza → el estado del pedido se sincroniza.
Datos ficticios en Room (`insertcode.db`): `data/local/` (Entidades, `OperacionDao`, `AppDatabase`), `repository/OperacionRepository.kt`, `viewmodel/OperacionViewModel.kt`.
Nota: el mapa de ruta usa coordenadas simuladas (no hay GPS real).
