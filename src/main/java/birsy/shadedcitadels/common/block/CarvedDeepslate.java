package birsy.shadedcitadels.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CarvedDeepslate extends DirectionalBlock {
    public static final DirectionProperty ROLL = DirectionProperty.create("roll", Direction.Plane.HORIZONTAL);

    public CarvedDeepslate(Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, ROLL);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction facing = pContext.getClickedFace();
        Direction roll = null;
        for (int i = 0; i < Direction.values().length; i++) {
            roll = align(facing, Direction.orderedByNearest(pContext.getPlayer())[i]);
            if (roll != Direction.UP && roll != Direction.DOWN) {
                break;
            }
        }

        return this.defaultBlockState().setValue(FACING, facing).setValue(ROLL, roll);
    }

    public static Direction align(Direction upDirection, Direction direction) {
        // :~~^             .............
        //.!~~!.         ..^~!!!~~!~~!~~^^:.
        //  ..         .:~7JY5YYYYYJJJJJ??!^.
        //          ..^~!?5GBBGGBBGPPPPYJ?!^..
        //         .:^~!7YGBP5PGBBBGGGP5?^^:...
        //         .:::~!YGYY<O>75GPPY:<O>:::..
        //          ...:~YJ?5YJ?YB&&&G~:.^:::.
        //             .^?YG#&&&&@@&&&#PJ7~^:.
        //          ...:~YB&&&@&&#BGP5YPGG?:..
        //          ..:^?PB#&&#BGG5~:.|J5P5^..
        //         ...::!YGB#BYJ?JJ7~!!!?Y?:....
        //         ..:^^^!YPPY?YPP5YYY?!7?!:^:..       .....
        //.       ..:^!77~~!77?5G5J7J?!~^:^^!~::............
        //...     ..:~7??JJ!77~7?7~~^::::^~!~^^:............
        //..........:~7?BB&BGGP5!YGP?J?Y5!JP5?::............
        //..........:~7?PBBBGGBB?PGPPG5##77YPJ::::..:.:.....
        //........:7~~!?JJY555555YJJJ??J7557777~!P7:::::::::
        //:.......:!                            J55~::::::::
        //::::::::::  this code fucked up bruh  ::::::::::::
        //::::::::::                            ::::::::::::
        //^^^^::^^^^^^^^^~~!!!~^^^~!!~~~^^^^^^^^^^^^^:^^^^^^
        //^^^^^^^^^^^^^^^^^^^^^^^^^~~~~^^^^^^^^^^^^^^^^^^^^^
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //
        //should fix this later but if it works, it works

        switch (upDirection) {
            case NORTH:
                switch (direction) {
                    case UP:    return Direction.NORTH;
                    case DOWN:  return Direction.SOUTH;
                    case NORTH: return Direction.UP;
                    case EAST:  return Direction.EAST;
                    case SOUTH: return Direction.DOWN;
                    case WEST:  return Direction.WEST;
                }
                break;
            case SOUTH:
                switch (direction) {
                    case UP:    return Direction.NORTH;
                    case DOWN:  return Direction.SOUTH;
                    case NORTH: return Direction.DOWN;
                    case EAST:  return Direction.WEST;
                    case SOUTH: return Direction.UP;
                    case WEST:  return Direction.EAST;
                }
                break;
            case EAST:
                switch (direction) {
                    case UP:    return Direction.NORTH;
                    case DOWN:  return Direction.SOUTH;
                    case NORTH: return Direction.WEST;
                    case EAST:  return Direction.UP;
                    case SOUTH: return Direction.EAST;
                    case WEST:  return Direction.DOWN;
                }
                break;
            case WEST:
                switch (direction) {
                    case UP:    return Direction.NORTH;
                    case DOWN:  return Direction.SOUTH;
                    case NORTH: return Direction.EAST;
                    case EAST:  return Direction.DOWN;
                    case SOUTH: return Direction.WEST;
                    case WEST:  return Direction.UP;
                }
                break;
            case UP:
                return direction.getOpposite();
            case DOWN:
                return direction;
        }
        return direction;
    }
}
