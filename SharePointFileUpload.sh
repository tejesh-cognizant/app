# Add references to SharePoint client assemblies
Add-Type -Path "C:\Program Files\Common Files\Microsoft Shared\Web Server Extensions\16\ISAPI\Microsoft.SharePoint.Client.dll"
Add-Type -Path "C:\Program Files\Common Files\Microsoft Shared\Web Server Extensions\16\ISAPI\Microsoft.SharePoint.Client.Runtime.dll"

# Set SharePoint site URL and credentials
$siteUrl = "https://your-sharepoint-site-url"
$username = "your-username"
$password = "your-password"

# Path to the file you want to upload
$filePath = "C:\path\to\your\file.txt"

# Get the content of the file
$fileContent = [System.IO.File]::ReadAllBytes($filePath)

# Connect to the SharePoint site
$context = New-Object Microsoft.SharePoint.Client.ClientContext($siteUrl)
$credentials = New-Object Microsoft.SharePoint.Client.SharePointOnlineCredentials($username, (ConvertTo-SecureString $password -AsPlainText -Force))
$context.Credentials = $credentials

# Get the document library
$docLibrary = $context.Web.Lists.GetByTitle("Your Document Library Name")

# Prepare file creation information
$fileCreationInfo = New-Object Microsoft.SharePoint.Client.FileCreationInformation
$fileCreationInfo.Url = (Get-Item $filePath).Name
$fileCreationInfo.Content = $fileContent
$fileCreationInfo.Overwrite = $true

# Add the file to the document library
$uploadedFile = $docLibrary.RootFolder.Files.Add($fileCreationInfo)
$context.Load($uploadedFile)
$context.ExecuteQuery()

Write-Host "File uploaded successfully."
