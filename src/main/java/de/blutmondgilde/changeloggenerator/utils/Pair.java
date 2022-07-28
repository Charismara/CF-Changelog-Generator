package de.blutmondgilde.changeloggenerator.utils;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class Pair<L,R> {
    @Getter
    private final L left;
    @Getter
    private final R right;
}
