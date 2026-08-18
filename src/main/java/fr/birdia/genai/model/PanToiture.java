package fr.birdia.genai.model;

import java.util.List;

/** Mirrors bpartners-api's {@code ExportAreaPictureAnnotation3DPan}, used for the PDF export. */
public record PanToiture(
    String imageUri,
    String name,
    Polygon polygon,
    Polygon orientedPolygon,
    List<Measurement> measurements,
    List<PanInfo> infos) {}
