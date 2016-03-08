$dummyRootFolder = Join-Path $PWD.Path "DummyMp3Files"


function RemoveExistingDummyRootFolder()
{
    Remove-Item -Recurse -Force $dummyRootFolder
}

function CreateRootFolder()
{
    New-Item -ItemType Directory $dummyRootFolder
}




RemoveExistingDummyRootFolder
CreateRootFolder


