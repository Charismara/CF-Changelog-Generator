package de.blutmondgilde.changeloggenerator.utils;

import java.util.Optional;

public class ArgumentUtils {
    public static Optional<String> getArgument(String[] args, String target) {
        Optional<String> result = Optional.empty();

        for (int i = 0; i < args.length; i++) {
            String value = args[i];
            if (target.equals(value) && args.length > i + 1) {
                String nextValue = args[i + 1];
                if (nextValue.startsWith("-")) continue;
                result = Optional.of(nextValue);
            }
        }

        return result;
    }
}
