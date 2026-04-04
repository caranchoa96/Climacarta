robocopy "..\jre" "dist\jre" /e
cd "/home/moeko/Downloads/launch4j/"
launch4jc.exe "C:\Users\Usuario\OneDrive\Documentos\GitHub\pruebaserve2\launcher.xml"
cd C:\Users\Usuario\OneDrive\Documentos\GitHub\pruebaserve2\dist
del launch4j.log
del ponce7.jar
del README.TXT
cd C:\Program Files (x86)\Inno Setup 6
ISCC.exe "C:\Users\Usuario\OneDrive\Documentos\GitHub\pruebaserve2\inosetup.iss"