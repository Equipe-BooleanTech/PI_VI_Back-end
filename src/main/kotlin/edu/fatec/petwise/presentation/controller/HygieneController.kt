import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.application.dto.HygieneRequest
import edu.fatec.petwise.application.usecase.CreateHygieneUseCase
import edu.fatec.petwise.application.usecase.DeleteHygieneUseCase
import edu.fatec.petwise.application.usecase.GetHygieneByIdUseCase
import edu.fatec.petwise.application.usecase.ListHygieneUseCase
import edu.fatec.petwise.application.usecase.UpdateHygieneUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/hygiene")
@CrossOrigin(origins = ["*"])
class HygieneController(
    private val createHygieneUseCase: CreateHygieneUseCase,
    private val listHygieneUseCase: ListHygieneUseCase,
    private val getHygieneByIdUseCase: GetHygieneByIdUseCase,
    private val updateHygieneUseCase: UpdateHygieneUseCase,
    private val deleteHygieneUseCase: DeleteHygieneUseCase
) {

    @GetMapping
    fun listHygiene(
        authentication: Authentication,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) searchQuery: String?,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<List<HygieneResponse>> {
        val hygiene = listHygieneUseCase.execute(authentication, category, searchQuery, activeOnly)
        return ResponseEntity.ok(hygiene)
    }

    @PostMapping
    fun createHygiene(
        @Valid @RequestBody request: HygieneRequest,
        authentication: Authentication
    ): ResponseEntity<HygieneResponse> {
        val hygiene = createHygieneUseCase.execute(request)
        return ResponseEntity.ok(hygiene)
    }

    @GetMapping("/{id}")
    fun getHygieneDetails(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<HygieneResponse> {
        val hygiene = getHygieneByIdUseCase.execute(id)
            ?: throw IllegalArgumentException("Produto de higiene não encontrado")

        return ResponseEntity.ok(hygiene)
    }

    @PutMapping("/{id}")
    fun updateHygiene(
        @PathVariable id: UUID,
        @Valid @RequestBody request: HygieneRequest,
        authentication: Authentication
    ): ResponseEntity<HygieneResponse> {
        val hygiene = updateHygieneUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(hygiene)
    }

    @DeleteMapping("/{id}")
    fun deleteHygiene(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteHygieneUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
