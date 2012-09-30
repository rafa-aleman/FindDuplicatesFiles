package FindDuplicatesFiles.src
/**
 * @author raleman
 */

//http://groovy.codehaus.org/Calculating+a+SHA1+hash+for+large+files

import java.security.MessageDigest

def findDuplicates(File directory, Map filesMap) {

    int KB = 1024
    int MB = 1024 * KB

    directory.eachFile {  file ->
        if(file.isFile() ) {
            def sha1
            if (file.size() > MB) sha1 = sha1File(file, MB)
            else sha1 = sha1File(file, KB)
            def listMaps = filesMap[sha1]
            if (listMaps == null) listMaps = []
            listMaps.add(file)
            filesMap.put(sha1, listMaps)
        }
        else{
            assert file.isDirectory()
            findDuplicates(file, filesMap)
        }
    }
}

def sha1File(File file, int size) {

    def messageDigest = MessageDigest.getInstance("SHA1")

    file.eachByte(size) { byte[] buf, int bytesRead ->
        messageDigest.update(buf, 0, bytesRead);
    }

    def sha1Hex = new BigInteger(1, messageDigest.digest()).toString(16).padLeft(40, '0')

    sha1Hex
}
def filesMap = [:]
def dir = "." //args
File directory = new File(dir)
if (!directory.exists() || !directory.isDirectory()) {
    println "Invalid directory $file provided"
    println "Usage: groovy FindDuplicatesFiles.groovy <directory_to_get_hashs>"
}
else{
    long start = System.currentTimeMillis()
    findDuplicates(directory, filesMap)
    long delta = System.currentTimeMillis() - start

    println "FindDuplicatesFiles took $delta ms to calculate the directory \".\" "

    filesMap.each{
        if(it.value.size()> 1)
            println "${it.key} -> ${it.value}"
    }
}

