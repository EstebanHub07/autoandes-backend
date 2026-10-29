# Cambia la conexión en application.yaml o define DB_URL, DB_USER y DB_PASSWORD.
# Genera una clave local temporal; define JWT_SECRET si debe persistir entre reinicios.
if (-not $env:JWT_SECRET) {
    $jwtBytes = New-Object byte[] 48
    $jwtRandom = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $jwtRandom.GetBytes($jwtBytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
    $jwtRandom.Dispose()
}
& "$PSScriptRoot/mvnw.cmd" spring-boot:run
exit $LASTEXITCODE
