@echo off
set kotlin_cli_version=0.12.2
set kotlin_cli_sha256=9fec42de4f378a27240b78c1b66ffef0c2aca66a4a6390ca67fe51aacb75677e
python "%~dp0scripts\kotlin_toolchain.py" %*
exit /b %ERRORLEVEL%
