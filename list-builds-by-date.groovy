import java.time.*

def jobPattern = "gse-2021-[a-zA-Z0-9]+-ms0\$"


println "Matching all Jobs to: '${jobPattern}'"
def matchedJobs = Jenkins.instance.items.findAll { job ->
    job.name =~ /$jobPattern/
}

def success = new TreeMap();
def failed = new TreeMap();

matchedJobs.each{
  it.getBuilds().each{
    date = it.time.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    def s = it.result == Result.SUCCESS ? 1 : 0
    def f = it.result == Result.SUCCESS ? 0 : 1
    
    success.merge(date, s, Integer.&sum)
    failed.merge(date, f, Integer.&sum)
  }
}

println("Date, Success, Failed, Builds")
success.each{
  def date = it.key;
  def s = success.get(date)
  def f = failed.get(date)
  def b = s + f
  println("$date, $s, $f, $b")
}

return
