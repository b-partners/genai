package fr.birdia.genai.file.hash;

import fr.birdia.genai.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
