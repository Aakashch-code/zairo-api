package org.example.zairo.transaction.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.zairo.transaction.application.dto.SavingResponse;
import org.example.zairo.transaction.application.dto.TransactionFilterRequest;
import org.example.zairo.transaction.application.dto.TransactionRequest;
import org.example.zairo.transaction.application.dto.TransactionResponse;
import org.example.zairo.transaction.application.service.TransactionService;
import org.example.zairo.transaction.infrastructure.export.TransactionExportService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Manage financial transactions within a workspace")
@PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN','ROLE_ANALYST','ROLE_VIEWER')")
public class TransactionController {

    private final TransactionService service;
    private final TransactionExportService exportService;

    @GetMapping
    @Operation(summary = "List transactions", description = "Retrieves a paginated list of all workspace transactions. Supports optional keyword search.")
    public Page<TransactionResponse> findAll(
            @Parameter(description = "Optional search keyword (e.g., 'rent', 'salary')")
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return service.findAll(keyword, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction details", description = "Retrieves a specific transaction by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public TransactionResponse findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN')")
    @Operation(summary = "Record new transaction", description = "Creates a new transaction. Restricted to Organizers and Admins.")
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER', 'ROLE_ADMIN')")
    @Operation(summary = "Update transaction", description = "Modifies an existing transaction. Restricted to Organizers and Admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public TransactionResponse update(
            @PathVariable long id,
            @Valid @RequestBody TransactionRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN')")
    @Operation(summary = "Delete transaction", description = "Permanently removes a transaction. Restricted to Organizers and Admins.")
    @ApiResponse(responseCode = "204", description = "Transaction deleted")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

    @GetMapping("/net")
    @Operation(summary = "Calculate net position", description = "Returns the total net financial position (Income vs Expenses) for the workspace.")
    public SavingResponse netPosition() {
        return service.calculateNetPosition();
    }

    @PostMapping("/filter")
    @Operation(summary = "Advanced filter", description = "Filters transactions by date ranges, categories, and amounts.")
    public Page<TransactionResponse> filter(
            @Valid @RequestBody TransactionFilterRequest request,
            Pageable pageable
    ) {
        return service.filterTransactions(request, pageable);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN', 'ROLE_ANALYST')")
    @Operation(summary = "Export to PDF", description = "Generates a PDF report of all transactions. Restricted to Organizers, Admins, and Analysts.")
    public void exportTransactionsToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=transactions_" + LocalDate.now() + ".pdf"
        );
        exportService.generate(response, service.findAll(null, Pageable.unpaged()));
    }
}