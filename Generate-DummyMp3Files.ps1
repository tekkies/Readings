$rootFolder = "DummyMp3Files"


function CreateRootFolder()
{
    New-Item -ItemType Directory $rootFolder
}


#DeleteOld
CreateRootFolder


