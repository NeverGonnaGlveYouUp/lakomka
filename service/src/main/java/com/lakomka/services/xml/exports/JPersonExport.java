package com.lakomka.services.xml.exports;

import com.lakomka.dto.JpersonXmlDto;
import com.lakomka.models.person.JPerson;
import com.lakomka.services.S3Service;
import com.lakomka.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class JPersonExport extends AbstractXmlExport<JPerson, Object, JpersonXmlDto, Object> {

    @Autowired
    public JPersonExport(S3Service s3Service, FileUtil fileUtil) {
        super(s3Service, fileUtil);
    }

    public String safeExportXml(List<JPerson> jPersonList) {
        return super.safeExportXml(
                null,
                jPersonList,
                "jpersons",
                "jp",
                Function.identity(),
                JPerson::toJpersonXmlDto,
                this::partOfFileName
        );
    }

    /**
     * примеры:
     * <p>
     * если один контрагент
     * jp_inn[9876543210]_2025-11-22-12-44-05.xml
     * <p>
     * если список контрагентов
     * jp_list[3]_2025-11-22-12-44-35.xml
     *
     * @param jPersonList - список контрагентов
     * @return - имя файла
     */
    private String partOfFileName(List<JPerson> jPersonList) {
        if (jPersonList.size() == 1) {
            return jPersonList.stream().findFirst()
                    .map(JPerson::getINN)
                    .map(inn -> "inn[" + inn + "]")
                    .orElse("inn[]");
        } else if (jPersonList.isEmpty()) {
            return "";
        } else {
            return "list[" + jPersonList.size() + "]";
        }
    }

}