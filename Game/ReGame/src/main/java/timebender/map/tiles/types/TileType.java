package timebender.map.tiles.types;

public enum TileType {
    NoTile(0),
    PlatformLeftTile(2),
    PlatformMiddleTile(3),
    PlatformRightTile(4),
    PlatformLonelyTile(5),
    TopAcidTile(6),
    AcidTile(7),
    BlackBoxLeft(8),
    BlackBoxRight(9),
    SpikesTile(10),
    NormalBoxLeft(11),
    NormalBoxRight(12),
    NormalBoxMiddle(13),
    PlatformWholeLeft(14),
    PlatformWholeMiddle(15),
    PlatformWholeRight(16),
    PlatformWholeMarginLeft(17),
    PlatformWholeMarginRight(18),
    HalfNormalBoxLeft(19),
    HalfNormalBoxMiddle(20),
    HalfNormalBoxRight(21),
    TunnelBottom(22),
    TunnelTop(23),
    TunnelTopLight(24),
    NoTileColor(25);

    private final int value;
    TileType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }

}
