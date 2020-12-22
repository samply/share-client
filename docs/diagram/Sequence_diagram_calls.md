REST-API calls of the [sequence diagram](Sequence diagram.png).


#### 1) GET /broker/rest/searchbroker/inquiries HTTP /1.1
Authorization:  Samply (Authtoken) (e.g. dXZ9KIGn3AuhIszhrIy590g8XqCXN)

#####Response:
HTTP/1.1 200 OK

Content-Type: application/xml


    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Inquiries>
        <Inquiry>
            <Id>2353</Id>
            <Revision>1</Revision>
        </Inquiry>
    </Inquiries>


#### 2) GET /broker/rest/searchbroker/inquiries/{inquiryId} HTTP/1.1
Authorization: Samply (Authtoken) (e.g. dXZ9KIGn3AuhIszhrIy590g8XqCXN)

query-language: CQL


#####Response:
HTTP/1.1 200

Content-Type: application/xml

    <ns9:Inquiry id="2353" revision="1" xmlns:ns6="http://schema.samply.de/common/RangeAttribute" xmlns:ns5="http://schema.samply.de/common/MultivalueAttribute" xmlns:ns8="http://schema.samply.de/common/Error" xmlns:ns7="http://schema.samply.de/common/Query" xmlns:ns9="http://schema.samply.de/common/Inquiry" xmlns:ns11="http://schema.samply.de/common/QueryResultStatistic" xmlns:ns10="http://schema.samply.de/cql/CqlQueryList" xmlns:ns2="http://schema.samply.de/common/MdrKey" xmlns:ns4="http://schema.samply.de/common/Attribute" xmlns:ns3="http://schema.samply.de/common/Value">

    Patient library Retrieve
    using FHIR version '4.0.0'
    include FHIRHelpers version '4.0.0'
    
    codesystem SampleMaterialType: 'https://fhir.bbmri.de/CodeSystem/SampleMaterialType'
    
    context Patient
    
    
    define InInitialPopulation:
      true
    
    define Gender:
      Patient.gender
    
    define AgeClass:
      (AgeInYears() div 10) * 10
        </cql></query><query><entityType>Specimen</entityType><cql>
    
    library Retrieve
    using FHIR version '4.0.0'
    include FHIRHelpers version '4.0.0'
    
    codesystem SampleMaterialType: 'https://fhir.bbmri.de/CodeSystem/SampleMaterialType'
    
    context Specimen
    
    
    define InInitialPopulation:
      true
    
    define TypeCodes:
      from Specimen.type.coding C return FHIRHelpers.ToCode(C)
    
    define SampleMaterialTypeCategory:
      case
        when
          exists (TypeCodes intersect {
            Code 'whole-blood' from SampleMaterialType,
            Code 'bone-marrow' from SampleMaterialType,
            Code 'buffy-coat' from SampleMaterialType,
            Code 'peripheral-blood-cells-vital' from SampleMaterialType,
            Code 'blood-plasma' from SampleMaterialType,
            Code 'plasma-edta' from SampleMaterialType,
            Code 'plasma-citrat' from SampleMaterialType,
            Code 'plasma-heparin' from SampleMaterialType,
            Code 'plasma-cell-free' from SampleMaterialType,
            Code 'plasma-other' from SampleMaterialType,
            Code 'blood-serum' from SampleMaterialType,
            Code 'ascites' from SampleMaterialType,
            Code 'csf-liquor' from SampleMaterialType,
            Code 'urine' from SampleMaterialType,
            Code 'liquid-other' from SampleMaterialType
          })
        then 'liquid'
        when
          exists (TypeCodes intersect {
            Code 'tissue-ffpe' from SampleMaterialType,
            Code 'tumor-tissue-ffpe' from SampleMaterialType,
            Code 'normal-tissue-ffpe' from SampleMaterialType,
            Code 'other-tissue-ffpe' from SampleMaterialType,
            Code 'tissue-frozen' from SampleMaterialType,
            Code 'tumor-tissue-frozen' from SampleMaterialType,
            Code 'normal-tissue-frozen' from SampleMaterialType,
            Code 'other-tissue-frozen' from SampleMaterialType,
            Code 'tissue-other' from SampleMaterialType
          })
        then 'tissue'
        else 'other'
      end
        </cql></query></ns10:cqlQueryList><ns9:ExposeURL>http://samplelocator.test.bbmri.de:443/broker/rest/searchbroker/exposes/2353GBASearchbrokerno-reply@vm.vmitro.de>
        
        

#### 3) POST /Library HTTP/1.1
Content-Type: application/fhir+json

    {"resourceType":"Library","id":"0","content":[{"contentType":"text/cql","data":"bGlicmFyeSBSZXRyaWV2ZQp1c2luZyBGSElSIHZlcnNpb24gJzQuMC4wJwppbmNsdWRlIEZISVJIZWxwZXJzIHZlcnNpb24gJzQuMC4wJwo
    KY29kZXN5c3RlbSBTYW1wbGVNYXRlcmlhbFR5cGU6ICdodHRwczovL2ZoaXIuYmJtcmkuZGUvQ29kZVN5c3RlbS9TYW1wbGVNYXRlcmlhbFR5cGUnCgpjb250ZXh0IFBhdGllbnQKCgo
    KZGVmaW5lIEluSW5pdGlhbFBvcHVsYXRpb246CiAgdHJ1ZQoKZGVmaW5lIEdlbmRlcjoKICBQYXRpZW50LmdlbmRlcgoKZGVmaW5lIEFnZUNsYXNzOgogIChBZ2VJblllYXJzKCkgZGl2IDEwKSAqIDEwCiAgICA="}],
    "url":"urn:uuid:2477bf74-54ed-4558-9b8c-1930430c0f26"}

#####Response:
HTTP/1.1 201 Created

POST /Measure HTTP/1.1

Content-Type: application/fhir+json

Accept: application/fhir+json

    {"resourceType":"Measure","status":"active","subjectCodeableConcept":{"coding":[{"system":"http://hl7.org/fhir/resource-types","code":"Patient"}]},"library":["urn:uuid:2477bf74-54ed-4558-9b8c-1930430c0f26"],"scoring":{"coding":[{"system":"http://terminology.hl7.org/CodeSystem/measure-scoring","code":"cohort"}]},"group":[{"population":[{"code":{"coding":[{"system":"http://terminology.hl7.org/CodeSystem/measure-population","code":"initial-population"}]},"criteria":{"language":"text/cql","expression":"InInitialPopulation"}}],"stratifier":[{"code":{"text":"Gender"},"criteria":{"language":"text/cql","expression":"Gender"}},{"code":{"text":"Age"},"criteria":{"language":"text/cql","expression":"AgeClass"}}]}]}

#####Response:
HTTP/1.1 201 Created

Location: {ldmURL}/Measure/d0993ffc-3ac0-4c83-a52b-f73a50c8d4dc/_history/11748

#### 4) GET  /Measure/{measure-url}/$evaluate-measure?periodStart=2000&periodEnd=2019 HTTP/1.1  (measure-url without "/_history/{id}" )

#####Response:
HTTP/1.1 200 OK

    {"status":"complete","type":"summary","measure":"https://blaze.life.uni-leipzig.de/fhir/Measure/a8439b0e-53b8-4b3d-9e6a-26d016b394fa","date":"2020-11-20T11:18:00.832236Z","period":{"start":"2000","end":"2019"},"group":[{"population":[{"count":0,"code":{"coding":[{"system":"http://terminology.hl7.org/CodeSystem/measure-population","code":"initial-population"}]}}],"stratifier":[{"stratum":[],"code":[{"text":"SampleType"}]}]}],"resourceType":"MeasureReport"}


#### 5) PUT /broker/rest/searchbroker/inquiries/{inquiryId}/replies/{bankemail} HTTP/1.1
Authorization:  Samply (Authtoken) (e.g. dXZ9KIGn3AuhIszhrIy590g8XqCXN)

Content-Type: text/plain; charset=ISO-8859-1

    {"donor":{"label":"Donors","count":11780,"stratifications":[{"title":"Gender","strata":[{"label":"female","count":5813},{"label":"male","count":5861},{"label":"null","count":100}]},{"title":"Age","strata":[{"label":"0","count":1048},{"label":"10","count":1280},{"label":"100","count":357},{"label":"110","count":82},{"label":"20","count":1465},{"label":"30","count":1295},{"label":"40","count":1417},{"label":"50","count":1547},{"label":"60","count":1401},{"label":"70","count":986},{"label":"80","count":522},{"label":"90","count":374}]}]},"sample":{"label":"Samples","count":0,"stratifications":[{"title":"SampleType","strata":[]}]}}

#####Response:
HTTP/1.1 200 OK
