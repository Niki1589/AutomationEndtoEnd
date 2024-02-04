package automation.batch;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.util.BeanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CreateModelProfileApi {
    General General;
    Earthquake Earthquake;
    Flood Flood;
    Windstorm Windstorm;
    Terrorism Terrorism;
    SevereConvectiveStorm SevereConvectiveStorm;
}

class General {
    String subPeril;
    String peril;
    Boolean ignoreContractDates;
    Boolean excludePostalCodes;
    String engine;
    Boolean fireOnly;
    Integer alternateVulnCode;
    String LabelRegion;
    Integer numberOfSamples;
    String petName;
    Boolean run1dOnly;
    String name;
    String perilOverride;
    String petDataVersion;
    Integer numberOfPeriods;
    String insuranceType;
    String region;
    String analysisType;
    String locationPerRisk;
    Boolean applyPLA;
    String version;
    Integer endYear;
    PolicyCoverages policyCoverages;
    Integer eventRateSchemeId;
    String policyPerRisk;
    String description;
    String modelRegion;
    String subRegions;
    String analysisMode;
    Integer startYear;
}
class Earthquake {
    CalculateLossesFrom calculateLossesFrom;
    SecondaryPerils secondaryPerils;
    String gmpeName;
    Boolean applyPLA;
    String gmpeCode;

}
class CalculateLossesFrom {
    Boolean tsunami;
    Boolean shake;
    Boolean fire;
    Boolean flood;
    Boolean coastalFlood;
    Boolean wind;
    Boolean inlandFlood;
    Boolean tornado;
    Boolean hail;
    Boolean straightLineWind;
    @SerializedName("Aircraft Impact")
    Boolean Aircraft_Impact;
    Boolean Bombs;
    Boolean Conflagration;
    @SerializedName("Sabotage - Industrial Plant (explosion only)")
    Boolean Sabotage_Industrial_Plant_explosion_only;

}
class SecondaryPerils {
    Boolean landslide;
    Boolean liquefaction;
}

class Flood {
    CalculateLossesFrom calculateLossesFrom;
    Boolean applyPLA;
    Boolean includePluvial;
    Boolean includeBespokeDefence;
    Boolean defenceOn;
}
class Windstorm
{
    CalculateLossesFrom calculateLossesFrom;
    Boolean applyPLA;
}

class Terrorism
{
    CalculateLossesFrom calculateLossesFrom;
}

class SevereConvectiveStorm
{
    CalculateLossesFrom calculateLossesFrom;
    Boolean dynamicAutomobileModeling;
}

class PolicyCoverages {
    Boolean windstorm;
    Boolean flood;
}