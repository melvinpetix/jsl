@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml


def call(yaml){
  Yaml parser = new Yaml()

  config = parser.load( new File(yaml).text )

  println config.doesntExists ?: "doesnExists doesn't exists"

  println yaml.project_name
  
  println yaml.parameters

  println yaml.steps
}
