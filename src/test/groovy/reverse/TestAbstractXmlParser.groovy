package reverse

import java.nio.file.Path

import org.flightgear.clgen.reverse.AbstractXmlParser

import spock.lang.Specification

class TestAbstractXmlParser extends Specification {

    class Parser extends AbstractXmlParser {
        @Override
        void parse(Path path) {}
    }

    def parser = new Parser()

    def "Check simple strings can be quoted"() {
        expect: parser.quote('abc') == '"abc"'
        and:    parser.quote('a') == '"a"'
        and:    parser.quote('') == '""'
    }

    def "Check strings requiring escapes can be quoted"() {
        expect: parser.quote('abc"') == '"abc\\""'
        and:    parser.quote('ab\\c') == '"ab\\\\c"'
    }

}
