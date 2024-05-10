# Définir le répertoire source, temporaire et cible
$work_dir = "E:/itu/s4/web_dynamic/summer/__summer__"
$src_dir = "$work_dir/src"
$temp_dir = "$work_dir/src_temp"
$target_dir = "$work_dir/out"
$manifest_file = "$target_dir/MANIFEST.MF"

# Supprimer le dossier temporaire s'il existe déjà
echo "Debut de la compilation des fichiers .java ..."
if (Test-Path $temp_dir) {
    Remove-Item -Path $temp_dir -Recurse -Force
}
if (Test-Path $target_dir) {
    Remove-Item -Path $target_dir -Recurse -Force
}

# Créer le dossier temporaire
echo "Creation d'un dossier temporaire pour la compilation ..."
New-Item -ItemType Directory -Path $temp_dir

# Créer le dossier target
echo "Creation d'un dossier target pour les fichiers .class apres la compilation ..."
New-Item -ItemType Directory -Path $target_dir

# Copier tous les fichiers Java dans le dossier temporaire
Get-ChildItem -LiteralPath $src_dir -Recurse -Filter *.java | ForEach-Object {
    if ($_.BaseName -ne "Main") {
        Copy-Item -Path $_.FullName -Destination $temp_dir
    }
}

# Se déplacer dans le dossier temporaire
Set-Location -Path $temp_dir

# Compiler les fichiers.java et les envoyer vers $target_dir
echo "Fin de la compilation."
javac -d $target_dir *.java

# Retourner au répertoire parent
Set-Location -Path $work_dir

# Supprimer le dossier temporaire
echo "Suppression du dossier temporaire."
Remove-Item -Path $temp_dir -Recurse -Force

# Créer le fichier manifeste
echo "Creation du fichier MANIFEST..."
@"
Manifest-Version: 1.0
Built-By: Henintsoa Paul MANITRAJA
"@ > $manifest_file

# Créer le fichier JAR
echo "Création du jar..."
jar cf summer-framework.jar -C $target_dir.

# Effacer le dossier OUT
echo "Suppression du dossier OUT..."
Remove-Item -Path $target_dir -Recurse -Force

echo "Fin du build."