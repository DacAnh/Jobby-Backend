package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company){
        Company createdCompany =this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);

    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompany(
            @Filter Specification<Company> specification,
//            @RequestParam("current") Optional<String> currentOptional,
//            @RequestParam("pageSize") Optional<String> pageSizeOptional
            Pageable pageable
    ){
//        String sCurrent= currentOptional.isPresent() ? currentOptional.get() : "";
//        String sPageSize= pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
//        int current = Integer.parseInt(sCurrent);
//        int pageSize = Integer.parseInt(sPageSize);
//        Pageable pageable = PageRequest.of(current-1,pageSize);
        return ResponseEntity.ok(this.companyService.fetchAllCompany(specification,pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<Company> fetchCompany(@PathVariable("id") long id){
        Optional<Company> companyOptional = this.companyService.findById(id);
        return ResponseEntity.ok(companyOptional.get());
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company){
        return ResponseEntity.ok(this.companyService.updateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable long id){
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
