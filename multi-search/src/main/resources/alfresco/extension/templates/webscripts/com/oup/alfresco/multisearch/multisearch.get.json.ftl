{
	"urls" : [
	<#list urls as child>
		"url" : "${child}"<#if child_has_next>,</#if>
	</#list>
	]
}