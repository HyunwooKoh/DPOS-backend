package com.autohrsystem.ocr;

import com.autohrsystem.common.ReqType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class OcrParams {
    public String m_inputUri;
    public String m_outputUri;
    public String m_serverUrl;
    public String m_ext;
    public JsonObject m_reqOption;

    public OcrParams(String inputUri, String outputUri, String serverUrl, String ext) {
        m_inputUri = inputUri;
        m_outputUri = outputUri;
        m_serverUrl = serverUrl;
        m_reqOption = new JsonObject();
        m_ext = ext;
    }

    public boolean isValidReqType(String reqType) {
        return ReqType.isValidReqType(reqType);
    }

    public void setReqOption(String reqType) {
        // TODO: set region searchAPI by label me
        JsonArray searchAPIArr = new JsonArray();
        JsonArray key = new JsonArray();
        switch (reqType) {
            case ReqType.REQ_TYPE_PrsInfo -> {
                // set college
                JsonObject college = new JsonObject();
                college.put("Type", "FindRegion");
                college.put("Name", "college");
                key = new JsonArray("""
                        [
                                [
                                  167.391304347826,
                                  126.69565217391303
                                ],
                                [
                                  349.9999999999999,
                                  158.86956521739128
                                ]
                        ]""");
                college.put("Key", key);
                searchAPIArr.add(college);

                // set department
                JsonObject department = new JsonObject();
                department.put("Type", "FindRegion");
                department.put("Name", "department");
                key = new JsonArray("""
                        [
                                [
                                  168.26086956521738,
                                  163.2173913043478
                                ],
                                [
                                  351.7391304347825,
                                  191.04347826086953
                                ]
                        ]""");
                department.put("Key", key);
                searchAPIArr.add(department);

                // set studentID
                JsonObject studentID = new JsonObject();
                studentID.put("Type", "FindRegion");
                studentID.put("Name", "studentID");
                key = new JsonArray("""
                        [
                                 [
                                   164.78260869565213,
                                   197.13043478260866
                                 ],
                                 [
                                   298.695652173913,
                                   226.695652173913
                                 ]
                        ]""");
                studentID.put("Key", key);
                searchAPIArr.add(studentID);

                // set korName
                JsonObject korName = new JsonObject();
                korName.put("Type", "FindRegion");
                korName.put("Name", "korName");
                key = new JsonArray("""
                        [
                                [
                                  166.52173913043475,
                                  234.52173913043475
                                ],
                                [
                                  299.56521739130426,
                                  264.9565217391304
                                ]
                        ]""");
                korName.put("Key", key);
                searchAPIArr.add(korName);

                // set engName
                JsonObject engName = new JsonObject();
                engName.put("Type", "FindRegion");
                engName.put("Name", "engName");
                key = new JsonArray("""
                        [
                                [
                                  353.47826086956513,
                                  233.65217391304344
                                ],
                                [
                                  537.8260869565216,
                                  264.9565217391304
                                ]
                        ]""");
                engName.put("Key", key);
                searchAPIArr.add(engName);

                // set phone
                JsonObject phone = new JsonObject();
                phone.put("Type", "FindRegion");
                phone.put("Name", "phone");
                key = new JsonArray("""
                        [
                                 [
                                   352.6086956521739,
                                   195.39130434782606
                                 ],
                                 [
                                   537.8260869565216,
                                   230.17391304347822
                                 ]
                        ]""");
                phone.put("Key", key);
                searchAPIArr.add(phone);

                // set birth
                JsonObject birth = new JsonObject();
                birth.put("Type", "FindRegion");
                birth.put("Name", "birth");
                key = new JsonArray("""
                        [
                                 [
                                   167.391304347826,
                                   270.17391304347825
                                 ],
                                 [
                                   301.3043478260869,
                                   302.3478260869565
                                 ]
                        ]""");
                birth.put("Key", key);
                searchAPIArr.add(birth);

                // set beforeRevise
                JsonObject beforeRevise = new JsonObject();
                beforeRevise.put("Type", "FindRegion");
                beforeRevise.put("Name", "beforeRevise");
                key = new JsonArray("""
                        [
                                 [
                                   249.9999999999999,
                                   334.52173913043475
                                 ],
                                 [
                                   537.8260869565216,
                                   364.08695652173907
                                 ]
                        ]""");
                beforeRevise.put("Key", key);
                searchAPIArr.add(beforeRevise);

                // set afterRevise
                JsonObject afterRevise = new JsonObject();
                afterRevise.put("Type", "FindRegion");
                afterRevise.put("Name", "afterRevise");
                key = new JsonArray("""
                        [
                                 [
                                   249.9999999999999,
                                   369.30434782608694
                                 ],
                                 [
                                   536.9565217391304,
                                   398.86956521739125
                                 ]
                        ]""");
                afterRevise.put("Key", key);
                searchAPIArr.add(afterRevise);
                m_reqOption.put("SearchAPI", searchAPIArr);
            }
            case ReqType.REQ_TYPE_Resume -> {
                // set name
                JsonObject name = new JsonObject();
                name.put("Type", "FindRegion");
                name.put("Name", "name");
                key = new JsonArray("""
                        [
                                [
                                  172.60869565217388,
                                  159.7391304347826
                                ],
                                [
                                  304.78260869565213,
                                  174.52173913043475
                                ]
                        ]""");
                name.put("Key", key);
                searchAPIArr.add(name);

                // set gender
                JsonObject gender = new JsonObject();
                gender.put("Type", "FindRegion");
                gender.put("Name", "gender");
                key = new JsonArray("""
                        [
                                [
                                  383.2850241545894,
                                  158.45410628019326
                                ],
                                [
                                  520.4830917874397,
                                  176.32850241545896
                                ]
                        ]""");
                gender.put("Key", key);
                searchAPIArr.add(gender);

                // set volunteerArea
                JsonObject volunteerArea = new JsonObject();
                volunteerArea.put("Type", "FindRegion");
                volunteerArea.put("Name", "volunteerArea");
                key = new JsonArray("""
                        [
                                [
                                  170.1639344262295,
                                  176.72131147540983
                                ],
                                [
                                  303.9344262295082,
                                  195.08196721311472
                                ]
                        ]""");
                volunteerArea.put("Key", key);
                searchAPIArr.add(volunteerArea);

                // set experienced
                JsonObject experienced = new JsonObject();
                experienced.put("Type", "FindRegion");
                experienced.put("Name", "experienced");
                key = new JsonArray("""
                        [
                                [
                                  384.78260869565213,
                                  177.99999999999997
                                ],
                                [
                                  521.3043478260869,
                                  193.65217391304347
                                ]
                        ]""");
                experienced.put("Key", key);
                searchAPIArr.add(experienced);

                // set birth
                JsonObject birth = new JsonObject();
                birth.put("Type", "FindRegion");
                birth.put("Name", "birth");
                key = new JsonArray("""
                        [
                                 [
                                   228.1967213114754,
                                   265.57377049180326
                                 ],
                                 [
                                   523.9344262295082,
                                   282.9508196721311
                                 ]
                        ]""");
                birth.put("Key", key);
                searchAPIArr.add(birth);

                // set email
                JsonObject email = new JsonObject();
                email.put("Type", "FindRegion");
                email.put("Name", "email");
                key = new JsonArray("""
                        [
                                 [
                                   228.86904761904762,
                                   283.33333333333337
                                 ],
                                 [
                                   523.5119047619048,
                                   300.0
                                 ]
                        ]""");
                email.put("Key", key);
                searchAPIArr.add(email);

                // set phone
                JsonObject phone = new JsonObject();
                phone.put("Type", "FindRegion");
                phone.put("Name", "phone");
                key = new JsonArray("""
                        [
                                 [
                                   405.92031872509955,
                                   300.79681274900395
                                 ],
                                 [
                                   523.8486055776892,
                                   319.92031872509955
                                 ]
                        ]""");
                phone.put("Key", key);
                searchAPIArr.add(phone);

                // set address
                JsonObject address = new JsonObject();
                address.put("Type", "FindRegion");
                address.put("Name", "address");
                key = new JsonArray("""
                        [
                                [
                                  228.96103896103892,
                                  322.72727272727275
                                ],
                                [
                                  521.8181818181818,
                                  341.55844155844153
                                ]
                        ]""");
                address.put("Key", key);
                searchAPIArr.add(address);

                // set univScore
                JsonObject univScore = new JsonObject();
                univScore.put("Type", "FindRegion");
                univScore.put("Name", "univScore");
                key = new JsonArray("""
                        [
                                 [
                                   418.45238095238096,
                                   533.6309523809524
                                 ],
                                 [
                                   481.84523809523813,
                                   551.4880952380953
                                 ]
                        ]""");
                univScore.put("Key", key);
                searchAPIArr.add(univScore);
                m_reqOption.put("SearchAPI", searchAPIArr);
            }
            case ReqType.REQ_TYPE_3 -> m_reqOption = new JsonObject();
        }
    }

    public String targetPage(String reqType) {
        return switch (reqType) {
            case ReqType.REQ_TYPE_PrsInfo, ReqType.REQ_TYPE_Resume, ReqType.REQ_TYPE_3 -> "0";
            default -> "";
        };
    }
}
