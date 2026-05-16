package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.Company;

@Repository
// Thêm JpaSpecificationExecutor để kích hoạt filter
public interface CompanyRepository extends JpaRepository<Company,Long>, JpaSpecificationExecutor<Company> {
}
