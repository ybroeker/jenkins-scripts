import jenkins.model.*
import hudson.model.*
import hudson.triggers.*
import hudson.security.*
import jenkins.security.*
import hudson.plugins.git.*

def milestone = "ms0";
def templateJobName = "gse-2020-tut-explo-"+milestone;
def jobPattern = "gse-2020-[a-zA-Z0-9]+\$"

def templateJob = Jenkins.instance.getItemByFullName(templateJobName)


println "Matching all Jobs to: '${jobPattern}'"
def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}


matchedJobs.each { it ->
    if ((m = it.name =~ /gse-2020-([a-zA-Z0-9]+)/)) {
        def login = m[0][1]
        def newJobName = "gse-2020-"+login+"-"+milestone

        def newRepoUrl = "https://se.techfak.de/git/g-se-ws-2020-"+login
        def oldScm = templateJob.scm
        def newUserRemoteConfigs = oldScm.userRemoteConfigs.collect {
            new UserRemoteConfig(newRepoUrl, it.name, it.refspec, it.credentialsId)
        }
        def newScm = new GitSCM(newUserRemoteConfigs, oldScm.branches, oldScm.doGenerateSubmoduleConfigurations,
                oldScm.submoduleCfg, oldScm.browser, oldScm.gitTool, oldScm.extensions)


        def newJob = Jenkins.instance.copy(templateJob, newJobName);
        newJob.scm = newScm;

        def authorizationMatrixProperty = newJob.getProperty(AuthorizationMatrixProperty.class)
        authorizationMatrixProperty.add(hudson.model.Item.BUILD, login)
        authorizationMatrixProperty.add(hudson.model.Item.DISCOVER, login)
        authorizationMatrixProperty.add(hudson.model.Item.READ, login)

        newJob.save()
    }
}
