package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> specification, Pageable pageable){

        Page<Company> companyPage = this.companyRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(companyPage.getSize());

        meta.setTotal(companyPage.getTotalElements()+1);
        meta.setPages(pageable.getPageSize());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(companyPage.getContent());
        return resultPaginationDTO;
    }

    public Company handleCreateCompany(Company company){
        return this.companyRepository.save(company);
    }

    public Company updateCompany(Company company){
        Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
        if(companyOptional.isEmpty()){
            return null;
        }
        Company updatedCompany = companyOptional.get();
        updatedCompany.setName(company.getName());
        updatedCompany.setAddress(company.getAddress());
        updatedCompany.setDescription(company.getDescription());
        updatedCompany.setLogo(company.getLogo());
        return this.companyRepository.save(updatedCompany);
    }

    public void deleteCompany(long id){
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if(companyOptional.isEmpty()){
            Company company = companyOptional.get();

            List<User> users = company.getUsers();
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id){
        return this.companyRepository.findById(id);
    }
}
