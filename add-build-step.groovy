import hudson.triggers.*
import hudson.security.*
import jenkins.security.*
import hudson.plugins.git.*
import hudson.tasks.*

def jobPattern = "gse-2020-[a-zA-Z0-9]+-ms2\$"


println "Matching all Jobs to: '${jobPattern}'"
def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}


matchedJobs.each {
  def buildersList = it.buildersList;
  def maven = buildersList.get(2)
  buildersList.remove(maven)

  buildersList.add(new Maven("appassembler:create-repository -B -Djavafx.platform=win", null))
  buildersList.add(new Maven("appassembler:create-repository -B -Djavafx.platform=mac", null))
  buildersList.add(new Maven("verify javadoc:javadoc -B -Djavafx.platform=linux", null))

}

return
