# Add references to SharePoint client assemblies
Add-Type -Path "C:\Program Files\Common Files\Microsoft Shared\Web Server Extensions\16\ISAPI\Microsoft.SharePoint.Client.dll"
Add-Type -Path "C:\Program Files\Common Files\Microsoft Shared\Web Server Extensions\16\ISAPI\Microsoft.SharePoint.Client.Runtime.dll"

# Set SharePoint site URL and credentials
$siteUrl = "https://your-sharepoint-site-url"
$username = "your-username"
$password = "your-password"

# Path to the folder containing the files you want to upload
$folderPath = "C:\path\to\your\folder"

# Get files created today
$today = Get-Date -Format "yyyy-MM-dd"
$filesToUpload = Get-ChildItem $folderPath | Where-Object { $_.CreationTime.Date -eq $today }

# Connect to the SharePoint site
$context = New-Object Microsoft.SharePoint.Client.ClientContext($siteUrl)
$credentials = New-Object Microsoft.SharePoint.Client.SharePointOnlineCredentials($username, (ConvertTo-SecureString $password -AsPlainText -Force))
$context.Credentials = $credentials

# Get the document library
$docLibrary = $context.Web.Lists.GetByTitle("Your Document Library Name")

# Upload each file created today
foreach ($file in $filesToUpload) {
    # Get the content of the file
    $fileContent = [System.IO.File]::ReadAllBytes($file.FullName)

    # Prepare file creation information
    $fileCreationInfo = New-Object Microsoft.SharePoint.Client.FileCreationInformation
    $fileCreationInfo.Url = $file.Name
    $fileCreationInfo.Content = $fileContent
    $fileCreationInfo.Overwrite = $true

    # Add the file to the document library
    $uploadedFile = $docLibrary.RootFolder.Files.Add($fileCreationInfo)
    $context.Load($uploadedFile)
    $context.ExecuteQuery()

    Write-Host "File $($file.Name) uploaded successfully."
}
