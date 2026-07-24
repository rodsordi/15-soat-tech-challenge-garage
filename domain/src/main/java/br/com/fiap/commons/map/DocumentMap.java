package br.com.fiap.commons.map;

import org.mapstruct.Named;

public interface DocumentMap {

    @Named("formattedDocument")
    default String mapFormattedDocument(String value) {
        if (value == null)
            return null;

        var digits = value.replaceAll("\\D", "");

        if (digits.length() == 11)
            return digits.replaceFirst(
                    "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                    "$1.$2.$3-$4");

        if (digits.length() == 14)
            return digits.replaceFirst(
                    "(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})",
                    "$1.$2.$3/$4-$5");

        return value;
    }

    @Named("unformattedDocument")
    default String mapUnformattedDocument(String value) {
        if (value == null)
            return null;
        return value.replaceAll("\\D", "");
    }
}
