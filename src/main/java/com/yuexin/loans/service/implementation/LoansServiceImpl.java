package com.yuexin.loans.service.implementation;

import com.yuexin.loans.dto.LoansDto;
import com.yuexin.loans.entity.Loans;
import com.yuexin.loans.exception.LoanAlreadyExistsException;
import com.yuexin.loans.exception.ResourceNotFoundException;
import com.yuexin.loans.repository.LoansRepository;
import com.yuexin.loans.service.ILoansService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

import static com.yuexin.loans.constants.LoansConstant.HOME_LOAN;
import static com.yuexin.loans.constants.LoansConstant.NEW_LOAN_LIMIT;
import static com.yuexin.loans.mapper.LoansMapper.mapToLoansDto;
import static com.yuexin.loans.mapper.LoansMapper.mapToLoansEntity;

@Service
public class LoansServiceImpl implements ILoansService {
    private final LoansRepository loansRepository;

    public LoansServiceImpl(LoansRepository loansRepository) {
        this.loansRepository = loansRepository;
    }

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loans> optionalLoans= loansRepository.findByMobileNumber(mobileNumber);
        if(optionalLoans.isPresent()){
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber "+mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        return mapToLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoan(LoansDto loansDto) {
        Loans loans = loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "LoanNumber", loansDto.getLoanNumber()));
        mapToLoansEntity(loansDto, loans);
        loansRepository.save(loans);
        return  true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        loansRepository.deleteById(loans.getLoanId());
        return true;
    }

    private Loans createNewLoan(String mobileNumber) {
        Loans newLoan = new Loans();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(HOME_LOAN);
        newLoan.setTotalLoan(NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(NEW_LOAN_LIMIT);
        return newLoan;
    }

}
