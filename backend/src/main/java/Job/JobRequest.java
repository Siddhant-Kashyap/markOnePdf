package Job;

import com.markOne.api.Enum.ConversionType;
import lombok.Data;

import java.util.List;

@Data
public class JobRequest {
    private List<String> fileIds;
    private ConversionType conversionType;
}
