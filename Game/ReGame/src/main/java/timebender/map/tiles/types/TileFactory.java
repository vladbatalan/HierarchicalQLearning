package timebender.map.tiles.types;

import timebender.map.tiles.Tile;

public class TileFactory {
    public static Tile CreateTile(TileType tileType){

        return switch (tileType) {
            case NoTile -> new NoTile();
            case PlatformLeftTile -> new PlatformLeftTile();
            case PlatformRightTile -> new PlatformRightTile();
            case PlatformMiddleTile -> new PlatformMiddleTile();
            case PlatformLonelyTile -> new PlatformLonelyTile();
            case TopAcidTile -> new TopAcidTile();
            case AcidTile -> new AcidTile();
            case BlackBoxLeft -> new BlackBoxLeft();
            case BlackBoxRight -> new BlackBoxRight();
            case SpikesTile -> new SpikesTile();
            case NormalBoxLeft -> new NormalBoxLeft();
            case NormalBoxRight -> new NormalBoxRight();
            case NormalBoxMiddle -> new NormalBoxMiddle();
            case PlatformWholeLeft -> new PlatformWholeLeft();
            case PlatformWholeMiddle -> new PlatformWholeMiddle();
            case PlatformWholeRight -> new PlatformWholeRight();
            case PlatformWholeMarginLeft -> new PlatformWholeMarginLeft();
            case PlatformWholeMarginRight -> new PlatformWholeMarginRight();
            case HalfNormalBoxLeft -> new HalfNormalBoxLeft();
            case HalfNormalBoxMiddle -> new HalfNormalBoxMiddle();
            case HalfNormalBoxRight -> new HalfNormalBoxRight();
            case TunnelBottom -> new TunnelBottom();
            case TunnelTop -> new TunnelTop();
            case TunnelTopLight -> new TunnelTopLight();
            case NoTileColor -> new NoTileColor();
        };
    }

    public static Tile CreateTile(int tileValue){
        for(TileType type : TileType.values()){
            if(type.getValue() == tileValue){
               return TileFactory.CreateTile(type);
            }
        }
        return null;
    }


}
