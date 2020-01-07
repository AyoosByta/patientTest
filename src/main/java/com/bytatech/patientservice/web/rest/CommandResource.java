package com.bytatech.patientservice.web.rest;
import java.net.URI;
import  com.bytatech.patientservice.service.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bytatech.patientservice.client.auth_dms.api.AuthenticationApi;
import com.bytatech.patientservice.service.PatientService;
import com.bytatech.patientservice.service.CommandService;
import com.bytatech.patientservice.client.auth_dms.model.TicketBody;
import com.bytatech.patientservice.client.dmscore.model.SiteBodyCreate;

import com.bytatech.patientservice.client.dmscore.model.SiteMemberEntry;

import feign.Feign;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.jhipster.web.util.HeaderUtil;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.core.io.Resource;
import com.bytatech.patientservice.service.PatientService;
import com.bytatech.patientservice.web.rest.errors.BadRequestAlertException;
import com.bytatech.patientservice.service.dto.PatientDTO;
import com.bytatech.patientservice.service.dto.RecordDTO;
import com.bytatech.patientservice.service.dto.DMSRecordDTO;
@RestController
@RequestMapping("/api/commands")

public class CommandResource {

	
	/*
	
	
	@Autowired
	ApiKeyRequestInterceptor apiKeyRequestInterceptor;

	
	*/
	private String ticket;
	private Decoder decoder;

	private Encoder encoder;

	/*
	@Autowired
	SitesApi customSiteApi;
	@Autowired
	com.bytatech.ayoos.client.dms_core.api.SitesApi siteApi;
	@Autowired
	com.bytatech.ayoos.client.custom_dms_core.api.NodesApi nodesApi;
	
	@Autowired
	UserService userService;*/
	   private final Logger log = LoggerFactory.getLogger(CommandResource.class);
	 private static final String ENTITY_NAME = "patientmicroservicePatient";

	    @Value("${jhipster.clientApp.name}")
	    private String applicationName;
		@Autowired
	    private  PatientService patientService;
	
		@Autowired
		private CommandService commandService;
		
		@Autowired
		 private  DMSRecordService dMSRecordService;
		@Autowired
		private  MedicalCaseService medicalCaseService;
	
		@Autowired
		AuthenticationApi authenticationApi;
	
	@GetMapping("/patients")
	public String test() {
		return "success";
	}
	/**
	 * POST /patients : Create a new patient.
	 *
	 * @param patientDTO
	 *            the patientDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         patientDTO, or with status 400 (Bad Request) if the patient has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@PostMapping("/patients-dms")
	public ResponseEntity<PatientDTO> createPatientWithDMS(@RequestBody PatientDTO patientDTO) throws URISyntaxException {
		 log.debug("REST request to save Patient : {}", patientDTO);
	        if (patientDTO.getId() != null) {
	            throw new BadRequestAlertException("A new patient cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	     
	    commandService.createPersonOnDMS(patientDTO);

		String siteId = patientDTO.getIdpCode() + "site";

		String dmsId = commandService.createSite(siteId);
		patientDTO.setDmsId(dmsId);
	
		commandService.createSiteMembership(patientDTO.getDmsId(), patientDTO.getIdpCode());
		PatientDTO result = patientService.save(patientDTO);

		 return ResponseEntity.created(new URI("/api/patients/" + result.getId()))
		            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
		            .body(result);
			
	}
	
	/**
     * PUT  /patients : Updates an existing patient.
     *
     * @param patientDTO the patientDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated patientDTO,
     * or with status 400 (Bad Request) if the patientDTO is not valid,
     * or with status 500 (Internal Server Error) if the patientDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
  /*  @PutMapping("/patients")
    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientDTO patientDTO) throws URISyntaxException {
        log.debug("REST request to update Patient : {}", patientDTO);
        if (patientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PatientDTO result = patientService.save(patientDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, patientDTO.getId().toString()))
            .body(result);
    }

	
*/


	@GetMapping("createTicket/{userId}/{password}")
	public String createTicket(@PathVariable  String userId, @PathVariable String password) {

		TicketBody ticketBody = new TicketBody();
		ticketBody.setUserId(userId);
		ticketBody.setPassword(password);
		String tic = authenticationApi.createTicket(ticketBody).getBody().getEntry().getId();
		return tic;
	}

	
	

	  
    /**
     * POST  /dms-records : Create a new dMSRecord.
     *
     * @param dMSRecordDTO the dMSRecordDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dMSRecordDTO, or with status 400 (Bad Request) if the dMSRecord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	 @PostMapping("/dms-records")
	    public ResponseEntity<DMSRecordDTO> createDMSRecord(@RequestBody RecordDTO recordDTO) throws URISyntaxException {
		 log.debug("REST request to save DMSRecord : {}", recordDTO); 
		String prescriptionRef= commandService.addPrescriptionOnDMS(recordDTO.getFile(), recordDTO.getIdpCode());
		DMSRecordDTO dMSRecordDTO = new DMSRecordDTO();
		dMSRecordDTO.setMedicalCaseId(recordDTO.getMedicalCaseId());
		dMSRecordDTO.setPrescriptionRef(prescriptionRef);
		
	        if (dMSRecordDTO.getId() != null) {
	            throw new BadRequestAlertException("A new dMSRecord cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	        DMSRecordDTO result = dMSRecordService.save(dMSRecordDTO);
	        return ResponseEntity.created(new URI("/api/dms-records/" + result.getId()))
	                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
	                .body(result);
	    }
	 
	  /**
	     * POST  /medical-cases : Create a new medicalCase.
	     *
	     * @param medicalCaseDTO the medicalCaseDTO to create
	     * @return the ResponseEntity with status 201 (Created) and with body the new medicalCaseDTO, or with status 400 (Bad Request) if the medicalCase has already an ID
	     * @throws URISyntaxException if the Location URI syntax is incorrect
	     */
	/*    @PostMapping("/medical-cases")
	    public ResponseEntity<MedicalCaseDTO> createMedicalCase(@RequestBody MedicalCaseDTO medicalCaseDTO) throws URISyntaxException {
	        log.debug("REST request to save MedicalCase : {}", medicalCaseDTO);
	        if (medicalCaseDTO.getId() != null) {
	            throw new BadRequestAlertException("A new medicalCase cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	        MedicalCaseDTO result = medicalCaseService.save(medicalCaseDTO);
	        return ResponseEntity.created(new URI("/api/medical-cases/" + result.getId()))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
	            .body(result);
	    }
	 
	    */
	    /**
	     * POST  /patients : Create a new patient.
	     *
	     * @param patientDTO the patientDTO to create
	     * @return the ResponseEntity with status 201 (Created) and with body the new patientDTO, or with status 400 (Bad Request) if the patient has already an ID
	     * @throws URISyntaxException if the Location URI syntax is incorrect
	     */
	/*    @PostMapping("/patients")
	    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) throws URISyntaxException {
	        log.debug("REST request to save Patient : {}", patientDTO);
	        if (patientDTO.getId() != null) {
	            throw new BadRequestAlertException("A new patient cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	        PatientDTO result = patientService.save(patientDTO);
	        return ResponseEntity.created(new URI("/api/patients/" + result.getId()))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
	            .body(result);
	    }
*/

	    /**
	     * PUT  /patients : Updates an existing patient.
	     *
	     * @param patientDTO the patientDTO to update
	     * @return the ResponseEntity with status 200 (OK) and with body the updated patientDTO,
	     * or with status 400 (Bad Request) if the patientDTO is not valid,
	     * or with status 500 (Internal Server Error) if the patientDTO couldn't be updated
	     * @throws URISyntaxException if the Location URI syntax is incorrect
	     */
	 /*   @PutMapping("/patients")
	    public ResponseEntity<PatientDTO> updatePatient(@RequestBody PatientDTO patientDTO) throws URISyntaxException {
	        log.debug("REST request to update Patient : {}", patientDTO);
	        if (patientDTO.getId() == null) {
	            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
	        }
	        PatientDTO result = patientService.save(patientDTO);
	        return ResponseEntity.ok()
	            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, patientDTO.getId().toString()))
	            .body(result);
	    }*/
}
