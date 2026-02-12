package unfairweapons.weather;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class WeatherManager {

    private static final Map<ResourceKey<Level>, EldritchRain> ACTIVE_WEATHER = new HashMap<>();

    public static void tick(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();

        EldritchRain weather = ACTIVE_WEATHER.get(dimension);
        if (weather == null) return;

        weather.tick(level);

        if (weather.isFinished()) {
            ACTIVE_WEATHER.remove(dimension);
        }
    }

    public static void startWeather(ServerLevel level, EldritchRain weather) {
        ACTIVE_WEATHER.put(level.dimension(), weather);
    }

    public static boolean hasActiveWeather(ServerLevel level) {
        return ACTIVE_WEATHER.containsKey(level.dimension());
    }

    public static void stopWeather(ServerLevel level) {
        ACTIVE_WEATHER.remove(level.dimension());
    }
}