package br.com.fiap.commons.map;

import org.mapstruct.Named;

public interface CpfMap {

    @Named("formattedCpf")
    default String mapFormattedCpf(String value) {
        if (value == null)
            return null;

        var digits = value.replaceAll("\\D", "");
        if (digits.length() != 11)
            return value;

        return digits.replaceFirst(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4");
    }

    @Named("unformattedCpf")
    default String mapUnformattedCpf(String value) {
        if (value == null)
            return null;
        return value.replaceAll("\\D", "");
    }
}
