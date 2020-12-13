import java.time.*;
import hudson.plugins.sloccount.*;
  
def jobPattern = "gse-2020-[a-zA-Z0-9]+-ms1\$"


println "Matching all Jobs to: '${jobPattern}'"
def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}

projectsStats = matchedJobs.stream().map({it.lastSuccessfulBuild})
    .filter({it!=null})
    .map({it.getAction(SloccountBuildAction.class).result.report})
    .mapToInt({it.lineCount})
    .summaryStatistics()

fileStats = matchedJobs.stream().map({it.lastSuccessfulBuild})
    .filter({it!=null})
    .map({it.getAction(SloccountBuildAction.class).result.report})
    .flatMap({it.files.stream()})
    .mapToInt({it.lineCount})
    .summaryStatistics()

nrOfFiles = matchedJobs.stream().map({it.lastSuccessfulBuild})
    .filter({it!=null})
    .map({it.getAction(SloccountBuildAction.class).result.report})
    .mapToInt({it.files.size})
    .summaryStatistics()

println(" Zeilen pro Projekt: ${projectsStats}")
println("    Zeile pro Datei: ${fileStats}")
println("Dateien pro Projekt: ${nrOfFiles}")
