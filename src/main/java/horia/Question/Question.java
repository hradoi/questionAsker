package horia.Question;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Question {

    private static final String DOB_COMMAND_TEMPLATE = "" +
            "SELECT  ?dob \n" +
            "FROM <http://dbpedia.org> \n" +
            "WHERE { \n" +
            "   dbr:{{name}} dbo:birthDate ?dob . \n" +
            "} LIMIT 1 \n";

    private static final String BIRTH_NAME_COMMAND_TEMPLATE = "" +
            "SELECT  ?name \n" +
            "FROM <http://dbpedia.org> \n" +
            "WHERE { \n" +
            "   dbr:{{name}} ?p ?name . \n"+
            "   FILTER (?p IN (dbp:birthname, dbp:birthName, dbo:birthName,dbo:birthname) )\n" +
            "} LIMIT 1 \n";

    public static Object ask(String query){
        if (query.startsWith("How old is")) {
            Integer age = ageQuestion(query.substring("How old is ".length()));
            return age > -1 ? age : "No date of birth found for query \"" + query +"\"";
        }
        if (query.startsWith("What is the birth name of ")) {
            return nameQuestion(query.substring("What is the birth name of ".length()));
        }
        //I would have thrown an exception here, but it breaks the requirements
        return "This question was not defined.";
    }

    static Object nameQuestion(String person) {
        String parsedName = parseName(person);
        String nameCommand = BIRTH_NAME_COMMAND_TEMPLATE.replace("{{name}}", parsedName);
        QueryExecution exec = getQueryExecution(nameCommand);
        ResultSet results =  exec.execSelect();
        if (!results.hasNext()) {
            return "No full name was found for " + parsedName;
        }

        return results.next().get("name").asLiteral().getValue().toString();
    }

    static Integer ageQuestion(String person){
        String ageCommand = DOB_COMMAND_TEMPLATE.replace("{{name}}", parseName(person));
        QueryExecution exec = getQueryExecution(ageCommand);
        ResultSet results =  exec.execSelect();
        if (!results.hasNext()) {
            return -1;
        }
        String dob = results.next().get("dob").asLiteral().getValue().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dob, dtf);
        return ld.until(LocalDate.now()).getYears();
    }

    static String parseName(String person) {
        String trimmed = person.trim();
        String x = trimmed.endsWith("?") ?  trimmed.substring(0, trimmed.length() - 1).trim() : trimmed;
        String s = Arrays.stream(x.toLowerCase().split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining("_"));
        return s;
    }

    private static QueryExecution getQueryExecution(String command) {
        ParameterizedSparqlString qs = new ParameterizedSparqlString(command);
        qs.setNsPrefix("dbr", "http://dbpedia.org/resource/");
        qs.setNsPrefix("dbp", "http://dbpedia.org/property/");
        qs.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
        return QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", qs.asQuery() );
    }
}
