param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs = @("tasks")
)

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$workDir = Split-Path -Parent $projectDir
$jdkRoot = Join-Path $workDir "toolchains\jdk17"
$jdk = Get-ChildItem -LiteralPath $jdkRoot -Directory -ErrorAction SilentlyContinue |
    Where-Object { Test-Path (Join-Path $_.FullName "bin\java.exe") } |
    Select-Object -First 1

if (-not $jdk) {
    throw "JDK 17을 찾을 수 없습니다: $jdkRoot"
}

$env:JAVA_HOME = $jdk.FullName
$env:GRADLE_USER_HOME = Join-Path $workDir "gradle-project-cache"
$env:Path = "$(Join-Path $env:JAVA_HOME 'bin');$env:Path"

Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "Gradle arguments: $($GradleArgs -join ' ')"

& (Join-Path $projectDir "gradlew.bat") @GradleArgs
exit $LASTEXITCODE
