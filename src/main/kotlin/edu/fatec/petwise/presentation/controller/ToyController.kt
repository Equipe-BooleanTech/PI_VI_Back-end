import edu.fatec.petwise.application.dto.ToyResponse
import edu.fatec.petwise.application.dto.ToyRequest
import edu.fatec.petwise.application.usecase.CreateToyUseCase
import edu.fatec.petwise.application.usecase.DeleteToyUseCase
import edu.fatec.petwise.application.usecase.GetToyByIdUseCase
import edu.fatec.petwise.application.usecase.ListToyUseCase
import edu.fatec.petwise.application.usecase.UpdateToyUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/toys")
@CrossOrigin(origins = ["*"])
class ToyController(
    private val createToyUseCase: CreateToyUseCase,
    private val listToyUseCase: ListToyUseCase,
    private val getToyByIdUseCase: GetToyByIdUseCase,
    private val updateToyUseCase: UpdateToyUseCase,
    private val deleteToyUseCase: DeleteToyUseCase
) {

    @GetMapping
    fun listToys(
        authentication: Authentication,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) searchQuery: String?,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<List<ToyResponse>> {
        val toys = listToyUseCase.execute(authentication, category, searchQuery, activeOnly)
        return ResponseEntity.ok(toys)
    }

    @PostMapping
    fun createToy(
        @Valid @RequestBody request: ToyRequest,
        authentication: Authentication
    ): ResponseEntity<ToyResponse> {
        val toy = createToyUseCase.execute(request)
        return ResponseEntity.ok(toy)
    }

    @GetMapping("/{id}")
    fun getToyDetails(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<ToyResponse> {
        val toy = getToyByIdUseCase.execute(id)
            ?: throw IllegalArgumentException("Brinquedo não encontrado")

        return ResponseEntity.ok(toy)
    }

    @PutMapping("/{id}")
    fun updateToy(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ToyRequest,
        authentication: Authentication
    ): ResponseEntity<ToyResponse> {
        val toy = updateToyUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(toy)
    }

    @DeleteMapping("/{id}")
    fun deleteToy(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteToyUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
