import java.time.*

def jobPattern = "gse-2020-[a-zA-Z0-9]+\$"


println "Matching all Jobs to: '${jobPattern}'"
def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}

def result = new TreeMap();

matchedJobs.each{
    it.items.each{
        it.getBuilds().each{
            date = it.time.toInstant()
      			.atZone(ZoneId.systemDefault())
      			.toLocalDate()
          
            result.merge(date, 1, Integer.&sum)
        }
    }
}

result.each{
  println("$it.key, $it.value")
}
